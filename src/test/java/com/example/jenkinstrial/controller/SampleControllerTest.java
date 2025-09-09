package com.example.jenkinstrial.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SampleControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void setup(){
        SampleController sampleController = new SampleController();
        mockMvc = MockMvcBuilders.standaloneSetup(sampleController).build();

    }

    @Test
    public void testSayHello() throws Exception{
        mockMvc.perform(get("/jenkins")).andExpect(status().isOk())
                .andExpect(content().string("Jenkins"));
    }

}

