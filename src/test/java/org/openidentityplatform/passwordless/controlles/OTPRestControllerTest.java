package org.openidentityplatform.passwordless.controlles;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openidentityplatform.passwordless.configuration.OTPConfiguration;
import org.openidentityplatform.passwordless.models.SentOTP;
import org.openidentityplatform.passwordless.repositories.OTPSettingsRepository;
import org.openidentityplatform.passwordless.repositories.SentOTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@WebMvcTest(OTPRestController.class)
@Import({OTPConfiguration.class})
public class OTPRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SentOTPRepository sentOTPRepository;


    @Test
    public void testSend() throws Exception {
        String requestBody = "{\"destination\": \"+7999999999\"}";

        mvc.perform(post("/otp/v1/{settingId}/send", "sms")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId", notNullValue()));

    }

    @Test
    public void testVerify() throws Exception {

        String operationId = UUID.randomUUID().toString();
        String otp = "12345";
        SentOTP sentOTP = new SentOTP();
        sentOTP.setOperationId(operationId);
        sentOTP.setOTP(otp);
        sentOTPRepository.save(sentOTP);

        String requestBody = "{\"operationId\": \""+operationId+"\", \"otp\" : \""+otp+"\"}";

        mvc.perform(post("/otp/v1/verify", "sms")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified", is(true)));
    }

}