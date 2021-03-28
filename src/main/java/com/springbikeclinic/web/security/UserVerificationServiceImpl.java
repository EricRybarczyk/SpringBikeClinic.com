package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.domain.security.VerificationToken;
import com.springbikeclinic.web.repositories.security.UserRepository;
import com.springbikeclinic.web.repositories.security.VerificationTokenRepository;
import com.springbikeclinic.web.services.email.MessageContent;
import com.springbikeclinic.web.services.email.MessageContentService;
import com.springbikeclinic.web.services.email.SendMailParameters;
import com.springbikeclinic.web.services.email.SendMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserVerificationServiceImpl implements UserVerificationService {

    private final VerificationTokenRepository verificationTokenRepository;
    private final MessageContentService messageContentService;
    private final UserRepository userRepository;
    private final SendMailService sendMailService;

    @Value("${verification.email.sender.address}")
    private String senderEmailAddress;

    @Value("${verification.link.baseurl}")
    private String verificationBaseUrl;

    @Override
    public void initiateUserVerification(User user, String verificationPath) {
        String tokenValue = UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString();
        verificationTokenRepository.save(new VerificationToken(user, tokenValue));

        final MessageContent messageContent = messageContentService
                .getUserVerificationMessageBody(verificationBaseUrl + verificationPath + "?token=" + tokenValue);

        SendMailParameters sendMailParameters = SendMailParameters.builder()
                .recipientAddress(user.getEmail())
                .senderAddress(senderEmailAddress)
                .messageContent(messageContent)
                .build();

        sendMailService.sendMessage(sendMailParameters);

    }

    @Override
    @Transactional
    public UserVerificationResult verifyUser(String token) {
        final Optional<VerificationToken> optionalVerificationToken = verificationTokenRepository.findByToken(token);
        if (optionalVerificationToken.isPresent()) {

            final VerificationToken verificationToken = optionalVerificationToken.get();
            final User user = verificationToken.getUser();

            if (user.getEnabled()) {
                // already enabled, nothing to do
                return UserVerificationResult.UNNECESSARY;
            }

            if (LocalDateTime.now().isAfter(verificationToken.getExpirationDateTime())) {
                // keep it simple - user must register again if they let the token expire
                verificationTokenRepository.delete(verificationToken);
                user.getAuthorities().clear();
                userRepository.delete(user);
                return UserVerificationResult.EXPIRED;
            }

            user.setEnabled(true);
            userRepository.save(user);

            // clean up the token now that it has served its purpose
            verificationTokenRepository.delete(verificationToken);

            return UserVerificationResult.SUCCESS;
        }

        return UserVerificationResult.INVALID;
    }

}
