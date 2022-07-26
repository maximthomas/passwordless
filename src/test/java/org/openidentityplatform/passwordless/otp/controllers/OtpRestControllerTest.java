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
import org.openidentityplatform.passwordless.otp.models.SendOTPResult;
import org.openidentityplatform.passwordless.otp.models.VerifyOTPResult;
import org.openidentityplatform.passwordless.otp.services.OperationNotFoundException;
import org.openidentityplatform.passwordless.otp.services.OtpService;
import org.openidentityplatform.passwordless.otp.services.SenderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
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


    @Test
    public void testSend() throws Exception {
        Mockito.when(otpService.send(anyString(), anyString(), isNull())).thenReturn(
                new SendOTPResult(UUID.randomUUID().toString())
        );

        String requestBody = "{\"destination\": \"+7999999999\"}";

        mvc.perform(post("/otp/v1/{settingId}/send", "sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operationId", notNullValue()));

    }

    @Test
    public void testSenderNotFound() throws Exception {
        Mockito.when(otpService.send(anyString(), anyString(), isNull()))
                .thenThrow(new SenderNotFoundException());

        String requestBody = "{\"destination\": \"+7999999999\"}";

        mvc.perform(post("/otp/v1/{settingId}/send", "sms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    public void testVerify() throws Exception {

        Mockito.when(otpService.verify(anyString(), anyString()))
                .thenReturn(new VerifyOTPResult(true, "test@test.com"));

        UUID operationId = UUID.randomUUID();
        String otp = "12345";

        String requestBody = "{\"operationId\": \""+operationId+"\", \"otp\" : \""+otp+"\"}";

        mvc.perform(post("/otp/v1/verify")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified", is(true)));
    }

    @Test
    public void testVerifyNotFound() throws Exception {

        Mockito.when(otpService.verify(anyString(), anyString()))
                .thenThrow(new OperationNotFoundException());

        UUID operationId = UUID.randomUUID();
        String otp = "12345";

        String requestBody = "{\"operationId\": \""+operationId+"\", \"otp\" : \""+otp+"\"}";

        mvc.perform(post("/otp/v1/verify")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

}