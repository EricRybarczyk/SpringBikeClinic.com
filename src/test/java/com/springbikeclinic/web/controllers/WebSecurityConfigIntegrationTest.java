package com.springbikeclinic.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class WebSecurityConfigIntegrationTest {

    public static final String VALID_CUSTOMER_USERNAME = "bike@bike.com";
    public static final String VALID_CUSTOMER_PASSWORD_RAW = "password";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void performLoginWithValidCredentials_isSuccessfullyAuthenticated() throws Exception {
        mockMvc.perform(formLogin()
                .user(VALID_CUSTOMER_USERNAME)
                .password(VALID_CUSTOMER_PASSWORD_RAW))
                .andExpect(authenticated().withAuthenticationName(VALID_CUSTOMER_USERNAME));

    }

    @Test
    void performLoginWithInvalidCredentials_isNotAuthenticated() throws Exception {
        mockMvc.perform(formLogin()
                .user("Not-a-valid-username")
                .password("not-really-a-password"))
                .andExpect(unauthenticated());
    }

    @Test
    void getStaticResources_asUnauthenticatedRequest_isOk() throws Exception {
        mockMvc.perform(get("/css/custom.css"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/img/3bikes_sydandmacky_small.png"))
                .andExpect(status().isOk());
    }

    @Test
    void getProtectedResource_asUnauthenticatedRequest_isUnauthorized() throws Exception {
        mockMvc.perform(get("/services/schedule/1")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

}
