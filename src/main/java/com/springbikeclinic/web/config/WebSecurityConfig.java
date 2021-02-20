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
                            .mvcMatchers("/h2-console/**").permitAll();  // TODO: DO NOT USE IN PRODUCTION
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
