package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.CreateAccountDto;
import com.springbikeclinic.web.dto.LoginDto;
import com.springbikeclinic.web.security.StandAloneAuthenticator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserDetailsManager userDetailsManager;
    private final StandAloneAuthenticator standAloneAuthenticator;

    @RequestMapping("account")
    public String account(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("createAccountDto", new CreateAccountDto());
            model.addAttribute("loginDto", new LoginDto());
        } else {
            SecurityUser user = (SecurityUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
            model.addAttribute("createAccountDto",
                    CreateAccountDto.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getUsername())
                            .build());
        }

        return "account";
    }

    @PostMapping("account/create")
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

        // log them in
        standAloneAuthenticator.authenticateUser(request, createAccountDto.getEmail(), createAccountDto.getCreatePassword());

        // redirect so browser is not left on the /account/create transient path
        return "redirect:/account";
    }

}
