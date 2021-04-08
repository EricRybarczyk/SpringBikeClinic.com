package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.PasswordResetToken;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.exceptions.ExpiredTokenException;
import com.springbikeclinic.web.exceptions.NotFoundException;
import com.springbikeclinic.web.repositories.security.PasswordResetTokenRepository;
import com.springbikeclinic.web.repositories.security.UserRepository;
import com.springbikeclinic.web.services.email.MessageContent;
import com.springbikeclinic.web.services.email.MessageContentService;
import com.springbikeclinic.web.services.email.SendMailParameters;
import com.springbikeclinic.web.services.email.SendMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageContentService messageContentService;
    private final SendMailService sendMailService;


    @Value("${verification.email.sender.address}")
    private String senderEmailAddress;

    @Value("${verification.link.baseurl}")
    private String baseUrl;


    @Override
    public void initiatePasswordReset(User user, String passwordResetHandlerPath) {
        String tokenValue = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        passwordResetTokenRepository.save(new PasswordResetToken(user, tokenValue));

        final MessageContent passwordResetMessageContent = messageContentService
                .getPasswordResetMessageBody(baseUrl + passwordResetHandlerPath + "?token=" + tokenValue);

        SendMailParameters sendMailParameters = SendMailParameters.builder()
                .recipientAddress(user.getEmail())
                .senderAddress(senderEmailAddress)
                .messageContent(passwordResetMessageContent)
                .build();

        sendMailService.sendMessage(sendMailParameters);

    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow( () -> new NotFoundException("Invalid password reset token"));
        if (LocalDateTime.now().isAfter(passwordResetToken.getExpirationDateTime())) {
            throw new ExpiredTokenException("Expired password reset token");
        }
        return passwordResetToken;
    }

    @Override
    public void processPasswordReset(PasswordResetToken passwordResetToken, String newPassword) {
        final User user = userRepository.findById(passwordResetToken.getUser().getId())
                .orElseThrow(() -> new NotFoundException("User " + passwordResetToken.getUser().getId() + " Not Found"));

        user.setPassword(passwordEncoder.encode(newPassword));

        // also set all the enable-flags to true
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);

        userRepository.save(user);
    }

}
