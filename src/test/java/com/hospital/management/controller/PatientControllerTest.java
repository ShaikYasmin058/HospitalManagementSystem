package com.hospital.management.controller;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void patientFormIncludesDiseaseField() throws Exception {
        mockMvc.perform(get("/patients/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"disease\"")));
    }

    @Test
    void savePatientRedirectsAndStoresPatient() throws Exception {
        mockMvc.perform(post("/patients")
                .param("name", "John Doe")
                .param("age", "34")
                .param("gender", "Male")
                .param("phone", "1234567890")
                .param("disease", "Fever"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/patients"));
    }
}
