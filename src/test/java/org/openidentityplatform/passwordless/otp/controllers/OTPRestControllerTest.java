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
import org.openidentityplatform.passwordless.otp.configuration.OTPConfiguration;
import org.openidentityplatform.passwordless.otp.models.SentOTP;
import org.openidentityplatform.passwordless.otp.repositories.SentOTPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@WebMvcTest(OTPRestController.class)
@Import({OTPConfiguration.class, MailSenderAutoConfiguration.class})
public class OTPRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private SentOTPRepository sentOTPRepository;


    @Test
    public void testSend() throws Exception {
        String requestBody = "{\"destination\": \"+7999999999\"}";

        mvc.perform(post("/otp/v1/{settingId}/send", "sms")
                .contentType(MediaType.APPLICATION_JSON)
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
        sentOTP.setExpireTime(System.currentTimeMillis() + 1000);
        sentOTPRepository.save(sentOTP);

        String requestBody = "{\"operationId\": \""+operationId+"\", \"otp\" : \""+otp+"\"}";

        mvc.perform(post("/otp/v1/verify", "sms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.verified", is(true)));
    }

}