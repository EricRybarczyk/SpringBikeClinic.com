package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.security.*;
import com.springbikeclinic.web.dto.*;
import com.springbikeclinic.web.security.PasswordResetService;
import com.springbikeclinic.web.security.UserVerificationService;
import com.springbikeclinic.web.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;


@Controller
@RequestMapping("account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserDetailsManager userDetailsManager;
    private final UserService userService;
    private final UserVerificationService userVerificationService;
    private final PasswordResetService passwordResetService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT = "customerAccountDto";
    private static final String VERIFICATION_PATH = "/account/confirmToken";
    private static final String RESET_PASSWORD_PATH = "/account/resetPassword";

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setAllowedFields("email", "firstName", "lastName", "createPassword", "confirmPassword", "emailAddress", "token", "newPassword");
    }

    @GetMapping("")
    public String account(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("createAccountDto", new CreateAccountDto());
            model.addAttribute("loginDto", new LoginDto());
            return "account";
        } else {
            model.addAttribute(MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT, CustomerAccountDto.from(principal));
            return "account/details";
        }
    }

    @GetMapping("/details")
    public String accountDetail(Model model, Principal principal) {
        model.addAttribute(MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT, CustomerAccountDto.from(principal));
        return "account/details";
    }

    @PostMapping("/create")
    public String createAccount(final HttpServletRequest request, @ModelAttribute("createAccountDto") @Valid CreateAccountDto createAccountDto, BindingResult bindingResult, Model model) {
        log.debug("POST Request to create an account for username: {}", createAccountDto.getEmail());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));
            return "account";
        }

        if (userDetailsManager.userExists(createAccountDto.getEmail())) {
            model.addAttribute("duplicateEmailError", Boolean.TRUE);
            return "account";
        }

        final User user = User.builder()
                .email(createAccountDto.getEmail())
                .password(createAccountDto.getCreatePassword())
                .firstName(createAccountDto.getFirstName())
                .lastName(createAccountDto.getLastName())
                .build();

        userDetailsManager.createUser(new SecurityUser(user));

        User createdUser = ((SecurityUser) userDetailsManager.loadUserByUsername(createAccountDto.getEmail())).getUser();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, createdUser, VERIFICATION_PATH));

        // redirect so browser is not left on the /account/create transient path
        return "redirect:/account/pending";
    }

    @GetMapping("/pending")
    public String showAccountVerificationPending() {
        return "account/pending";
    }

    @GetMapping("/confirmToken")
    public String processVerificationToken(@RequestParam("token") String token) {
        switch (userVerificationService.verifyUser(token)) {
            case INVALID:
                return "account/invalid";
            case EXPIRED:
                return "account/expired";
            default:
                // intentionally covers SUCCESS and UNNECESSARY results
                return "redirect:/account/verified";
        }
    }

    @GetMapping("/verified")
    public String showAccountVerificationComplete() {
        return "account/verified";
    }

    @PostMapping("/update")
    public String updateExistingAccount(@ModelAttribute("customerAccountDto") @Valid CustomerAccountDto customerUpdateDto, Principal principal, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));
            return "account/details";
        }

        // make sure changes are only applied to existing logged-in user
        final SecurityUser securityUser = SecurityUser.from(principal);
        userService.updateUser(securityUser, customerUpdateDto);

        model.addAttribute("updateSuccessful", Boolean.TRUE);

        return "account/details";
    }

    @GetMapping("/reset")
    public String getResetPassword(Model model) {
        model.addAttribute("resetPasswordRequestDto", new ResetPasswordRequestDto());
        return "account/reset";
    }

    @PostMapping("/reset")
    public String requestResetPassword(@ModelAttribute("resetPasswordDto") @Valid ResetPasswordRequestDto resetPasswordRequestDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));
            return "account/reset";
        }
        try {
            final SecurityUser securityUser = (SecurityUser) userDetailsManager.loadUserByUsername(resetPasswordRequestDto.getEmailAddress());

            eventPublisher.publishEvent(new OnPasswordResetRequestEvent(this, securityUser.getUser(), RESET_PASSWORD_PATH));

            // redirect so browser is not left on the /account/reset transient path
            return "redirect:/account/reset/pending";

        } catch (UsernameNotFoundException e) {
            log.error("Requested username {} was not found, UsernameNotFoundException: {}", resetPasswordRequestDto.getEmailAddress(), e.getMessage());
            model.addAttribute("resetPasswordDto", new ResetPasswordRequestDto(resetPasswordRequestDto.getEmailAddress()));
            return "account/reset";
        }
    }

    @GetMapping("/reset/pending")
    public String showPasswordResetPending() {
        return "account/resetPending";
    }

    @GetMapping("/resetPassword")
    public String preparePasswordReset(@RequestParam("token") String token, Model model) {
        final PasswordResetToken passwordResetToken = passwordResetService.getPasswordResetToken(token);
        model.addAttribute("resetPasswordResultDto", new ResetPasswordResultDto(token));
        return "account/passwordReset";
    }

    @PostMapping("/resetPassword")
    public String processPasswordReset(@ModelAttribute("resetPasswordResultDto") @Valid ResetPasswordResultDto resetPasswordResultDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));
            return "account/passwordReset";
        }
        // re-verify the token
        final PasswordResetToken passwordResetToken = passwordResetService.getPasswordResetToken(resetPasswordResultDto.getToken());

        // change the password for the User
        passwordResetService.processPasswordReset(passwordResetToken, resetPasswordResultDto.getNewPassword());

        return "account/resetComplete";
    }
}
