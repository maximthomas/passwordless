/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openidentityplatform.passwordless.otp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.openidentityplatform.passwordless.otp.models.SendOtpResult;
import org.openidentityplatform.passwordless.otp.models.VerifyOtpResult;
import org.openidentityplatform.passwordless.otp.services.FrequentSendingForbidden;
import org.openidentityplatform.passwordless.otp.services.OtpService;
import org.openidentityplatform.passwordless.otp.services.OtpVerifyAttemptsExceeded;
import org.openidentityplatform.passwordless.otp.services.SenderNotFoundException;
import org.openidentityplatform.passwordless.otp.services.SessionNotFoundException;
import org.openidentityplatform.passwordless.otp.services.TemplateNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@WebMvcTest(OtpRestController.class)
public class OtpRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private OtpService otpService;

    final static String sessionId = UUID.randomUUID().toString();
    final static String destination = "+7999999999";
    final static Long resendAllowedAt = System.currentTimeMillis() + 5 * 1000 * 60;
    final static Integer remainingAttempts = 5;
    final static String SEND_REQUEST_BODY = """
            {
                "destination": "+7999999999",
                "sender": "sms"
            }
            """;

    final static String VERIFY_OTP_REQUEST_BODY = """
            {
                "sessionId": "%s",
                "otp": "123456"
            }
            """.formatted(sessionId);

    @Test
    void testSend() throws Exception {

        Mockito.when(otpService.send(anyString(), anyString())).thenReturn(
                new SendOtpResult(sessionId, destination, resendAllowedAt, remainingAttempts)
        );
        mvc.perform(post("/otp/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SEND_REQUEST_BODY))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.destination").value(destination))
                .andExpect(jsonPath("$.resendAllowedAt").value(resendAllowedAt))
                .andExpect(jsonPath("$.remainingAttempts").value(remainingAttempts));

    }

    @Test
    void testSend_SenderNotFound() throws Exception {
        Mockito.when(otpService.send(anyString(), anyString()))
                .thenThrow(new SenderNotFoundException());
        mvc.perform(post("/otp/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SEND_REQUEST_BODY))
                .andExpect(status().isNotFound());
    }

    @Test
    void testSend_TemplateNotFound() throws Exception {

        Mockito.when(otpService.send(anyString(), anyString()))
                .thenThrow(new TemplateNotFoundException());
        mvc.perform(post("/otp/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SEND_REQUEST_BODY))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isNotFound());

    }

    @Test
    void testSend_InvalidData() throws Exception {

        String requestBody = """
                {
                    "destination": "+7999999999",
                    "type": "bad"
                }
                """;

        mvc.perform(post("/otp/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSend_frequentForbidden() throws Exception {
        Mockito.when(otpService.send(anyString(), anyString()))
                .thenThrow(new FrequentSendingForbidden());
        mvc.perform(post("/otp/v1/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(SEND_REQUEST_BODY))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void testVerify() throws Exception {


        Mockito.when(otpService.verify(anyString(), anyString()))
                .thenReturn(new VerifyOtpResult(true, null));

        mvc.perform(post("/otp/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFY_OTP_REQUEST_BODY))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid", is(true)));
    }

    @Test
    void testVerify_SessionNotFound() throws Exception {
        Mockito.when(otpService.verify(anyString(), anyString()))
                .thenThrow(new SessionNotFoundException());

        mvc.perform(post("/otp/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFY_OTP_REQUEST_BODY))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void testVerify_attemptsExceeded() throws Exception {
        Mockito.when(otpService.verify(anyString(), anyString()))
                .thenThrow(new OtpVerifyAttemptsExceeded());
        mvc.perform(post("/otp/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VERIFY_OTP_REQUEST_BODY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", notNullValue()));
    }
}