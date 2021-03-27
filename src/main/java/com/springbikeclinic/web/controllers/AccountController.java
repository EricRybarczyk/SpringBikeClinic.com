package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.security.OnRegistrationCompleteEvent;
import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.CreateAccountDto;
import com.springbikeclinic.web.dto.CustomerAccountDto;
import com.springbikeclinic.web.dto.LoginDto;
import com.springbikeclinic.web.security.UserVerificationService;
import com.springbikeclinic.web.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;


@Controller
@RequestMapping("account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserDetailsManager userDetailsManager;
    private final UserService userService;
    private final UserVerificationService userVerificationService;
    private final ApplicationEventPublisher eventPublisher;

    private static final String MODEL_ATTRIBUTE_CUSTOMER_ACCOUNT = "customerAccountDto";
    private static final String VERIFICATION_PATH = "/account/confirmToken";

    @InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setAllowedFields("email", "firstName", "lastName", "createPassword", "confirmPassword");
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
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, createdUser, getVerificationUrl(request).toString()));

        // redirect so browser is not left on the /account/create transient path
        return "redirect:/account/pending";
    }

    private URL getVerificationUrl(HttpServletRequest request) {
        String hostname = request.getLocalName();
        if (hostname.equalsIgnoreCase("ip6-localhost")) {
            hostname = "localhost";
        }
        try {
            if (request.getServerPort() == 80) {
                // don't include port if it is default 80
                return new URL(request.getScheme(), hostname, VERIFICATION_PATH);
            }
            return new URL(request.getScheme(), hostname, request.getServerPort(), VERIFICATION_PATH);
        } catch (MalformedURLException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
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

}
