package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.commands.AccountCommand;
import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;

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
            model.addAttribute("account",
                    AccountCommand.builder()
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .email(user.getUsername())
                            .build());
        }
        else {
            model.addAttribute("account", new AccountCommand());
        }

        return "account";
    }

    @PostMapping("account/create")
    public String createAccount(final HttpServletRequest request, @ModelAttribute AccountCommand command) {
        log.debug("POST Request to create an account for username: {}", command.getEmail());

        // TODO: validation - password & confirm password must match

        // TODO: handle username already exists

        final User user = User.builder()
                .email(command.getEmail())
                .password(command.getPassword())
                .firstName(command.getFirstName())
                .lastName(command.getLastName())
                .build();

        userDetailsManager.createUser(new SecurityUser(user));

        // log them in
        authenticateUser(request, command.getEmail(), command.getPassword());

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
