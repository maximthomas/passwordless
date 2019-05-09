package org.openidentityplatform.passwordless.controlles;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@WebMvcTest(OTPRestController.class)
public class OTPRestControllerTest {

    @Autowired
    private MockMvc mvc;


    @Test
    public void evaluatePolicy() throws Exception {
        String requestBody = "{\"destination\": \"+7999999999\"}";

        mvc.perform(post("/otp/v1/{settingId}/send", "testToken")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId", notNullValue()));
    }

}