package com.springbikeclinic.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(IndexController.class)
class IndexControllerTest {

    private static final String GET_INDEX_PATH = "/";
    private static final String EXPECTED_GET_INDEX_VIEW_NAME = "index";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @WithMockUser("authenticatedUser")
    @Test
    void getIndexAsAuthenticatedUser_IsOk() throws Exception {
        mockMvc.perform(get(GET_INDEX_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPECTED_GET_INDEX_VIEW_NAME));
    }

    @Test
    void getIndexAsAnonymousUser_IsOk() throws Exception {
        mockMvc.perform(get(GET_INDEX_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPECTED_GET_INDEX_VIEW_NAME));
    }

}
