package com.springbikeclinic.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .authorizeRequests(authorize -> {
                    authorize
                            // antMatcher vs mvcMatcher: favor mvcMatcher - https://stackoverflow.com/a/57373627/798642
                            .mvcMatchers("/h2-console/**").permitAll()  // TODO: DO NOT USE IN PRODUCTION
                            .mvcMatchers("/", "/css/**", "/fonts/**", "/img/**").permitAll()
                            .mvcMatchers("/about", "/services", "/account", "/account/create").permitAll();
                    // NOTE: Currently we intentionally block access to /services/schedule since login will be required for this feature
                })
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .formLogin(loginConfigurer -> {
                    loginConfigurer
                            .loginProcessingUrl("/login")
                            .loginPage("/account").permitAll()
                            .successForwardUrl("/account")
                            .defaultSuccessUrl("/account")
                            .failureUrl("/account?error");
                })
                .logout(logoutConfigurer -> {
                    logoutConfigurer
                            // must specify GET since we are using a link (href) tag and Spring expects a POST by default
                            .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                            .logoutSuccessUrl("/account?logout")
                            .permitAll();
                })
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**");

        // H2 console config
        http.headers().frameOptions().sameOrigin();
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

}
