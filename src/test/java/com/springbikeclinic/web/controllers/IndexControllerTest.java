package com.springbikeclinic.web.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

    @Test
    void getIndex() throws Exception {
        mockMvc.perform(get(GET_INDEX_PATH))
                .andExpect(status().isOk())
                .andExpect(view().name(EXPECTED_GET_INDEX_VIEW_NAME));
    }

}
