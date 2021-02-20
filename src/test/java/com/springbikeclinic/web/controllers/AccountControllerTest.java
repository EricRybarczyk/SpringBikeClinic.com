package com.springbikeclinic.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AccountController.class)

class AccountControllerTest {

    private static final String GET_ACCOUNT_PATH = "/account";
    private static final String EXPECTED_ACCOUNT_VIEW_NAME = "account";

    @Autowired
    private MockMvc mockMvc;

    @WithMockUser("authenticatedUser")
    @Test
    void getAccount() throws Exception {
        mockMvc.perform(get(GET_ACCOUNT_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPECTED_ACCOUNT_VIEW_NAME));
    }

}
