package com.springbikeclinic.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AboutController.class)
class AboutControllerTest {

    private static final String GET_ABOUT_PATH = "/about";
    private static final String EXPECTED_ABOUT_VIEW_NAME = "about";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAbout() throws Exception {
        mockMvc.perform(get(GET_ABOUT_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPECTED_ABOUT_VIEW_NAME));
    }

}
