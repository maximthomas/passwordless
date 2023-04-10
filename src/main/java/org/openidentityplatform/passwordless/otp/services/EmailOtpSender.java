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

import lombok.extern.log4j.Log4j2;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

@Log4j2
public class EmailOtpSender implements OtpSender {

    private final MailSender mailSender;

    public EmailOtpSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOTP(String destination, String messageBody, String messageTitle) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(destination);
        msg.setText(messageBody);
        msg.setSubject(messageTitle);
        try{
            this.mailSender.send(msg);
        }
        catch (MailException ex) {
            log.error(ex);
        }
    }
}
