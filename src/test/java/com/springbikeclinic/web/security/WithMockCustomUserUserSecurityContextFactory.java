package com.springbikeclinic.web.security;

import com.springbikeclinic.web.domain.security.Authority;
import com.springbikeclinic.web.domain.security.SecurityUser;
import com.springbikeclinic.web.domain.security.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;


public class WithMockCustomUserUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {


    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authority authorityCustomer = Authority.builder().role("CUSTOMER").build();
        String password = "{noop}password";
        User principal = User.builder()
                .email("user@springbikeclinic.com")
                .password(password)
                .firstName("Firsty")
                .lastName("McLasty")
                .authority(authorityCustomer)
                .build();
        SecurityUser securityUser = new SecurityUser(principal);
        Authentication auth = new UsernamePasswordAuthenticationToken(securityUser, password, securityUser.getAuthorities());
        context.setAuthentication(auth);
        return context;
    }

}
