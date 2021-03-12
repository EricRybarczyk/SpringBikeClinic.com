package com.springbikeclinic.web.controllers;

import com.springbikeclinic.web.security.WithMockCustomUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


@WebMvcTest(BikeController.class)
@ExtendWith(MockitoExtension.class)
class BikeControllerTest {

    private static final String GET_BIKES_BASE_PATH = "/account/bikes";
    private static final String EXPECTED_ACCOUNT_BIKES_VIEW_NAME = "account/bikes";

    @Autowired
    private MockMvc mockMvc;

    // not used directly, but required by Spring Security for mock user authentication
    @MockBean
    private UserDetailsManager userDetailsManager;


    @WithMockCustomUser
    @Test
    void getAccountBikes_asAuthenticatedUser_isOk() throws Exception {
        mockMvc.perform(get(GET_BIKES_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPECTED_ACCOUNT_BIKES_VIEW_NAME));
    }
}
