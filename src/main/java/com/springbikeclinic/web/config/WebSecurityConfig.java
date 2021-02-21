package com.springbikeclinic.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
                            .mvcMatchers("/about", "/services", "/account").permitAll();
                    // NOTE: Currently we intentionally block access to /services/schedule since login will be required for this feature
                })
                .authorizeRequests()
                .anyRequest().authenticated().and()
                .formLogin().and()
                .httpBasic()
                .and().csrf().ignoringAntMatchers("/h2-console/**");

        // H2 console config
        http.headers().frameOptions().sameOrigin();
    }

}
