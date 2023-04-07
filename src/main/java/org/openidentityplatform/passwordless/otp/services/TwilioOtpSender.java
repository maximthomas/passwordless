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

package org.openidentityplatform.passwordless.otp.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;

@Log4j2
public class TwilioOtpSender implements OtpSender {

    @Value("#{environment.TWILIO_MESSAGING_SERVICE_SID}")
    private String MESSAGING_SERVICE_SID;

    @Value("#{environment.TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("#{environment.TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @PostConstruct
    private void init() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
    public void sendOTP(String destination, String messageBody, String messageTitle) {
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(destination),
                MESSAGING_SERVICE_SID,
                messageBody)
                .create();
        log.info("sent message {}",message.getSid());
    }
}
