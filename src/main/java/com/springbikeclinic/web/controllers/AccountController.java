package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import com.springbikeclinic.web.dto.CreateAccountDto;
import com.springbikeclinic.web.dto.LoginDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private final UserDetailsManager userDetailsManager;
    private final AuthenticationManager authenticationManager;

    @RequestMapping("account")
    public String account(Model model) {
        if (SecurityContextHolder.getContext().getAuthentication().isAuthenticated()
                && SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SecurityUser) {
            SecurityUser user = (SecurityUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            model.addAttribute("createAccountDto",
                    CreateAccountDto.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getUsername())
                            .build());
        }
        else {
            model.addAttribute("createAccountDto", new CreateAccountDto());
        }

        model.addAttribute("loginDto", new LoginDto());

        return "account";
    }

    @PostMapping("account/create")
    public String createAccount(final HttpServletRequest request, @ModelAttribute("createAccountDto") @Valid CreateAccountDto createAccountDto, BindingResult bindingResult, Model model) {
        log.debug("POST Request to create an account for username: {}", createAccountDto.getEmail());

        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(e -> log.debug(e.toString()));
            return "account";
        }

        // TODO: handle username already exists
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
        authenticateUser(request, createAccountDto.getEmail(), createAccountDto.getCreatePassword());

        return "redirect:/account";
    }

    private void authenticateUser(HttpServletRequest request, String username, String password) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);

        // generate session if one doesn't exist
        request.getSession();

        token.setDetails(new WebAuthenticationDetails(request));
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
