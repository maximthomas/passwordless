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

package org.openidentityplatform.passwordless.otp.configuration;

import org.openidentityplatform.passwordless.otp.services.DummyOtpSender;
import org.openidentityplatform.passwordless.otp.services.EmailOtpSender;
import org.openidentityplatform.passwordless.otp.services.OtpGenerator;
import org.openidentityplatform.passwordless.otp.services.OtpSender;
import org.openidentityplatform.passwordless.otp.services.TwilioOtpSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

@Configuration
public class OtpBeansConfiguration {
    @Bean
    public OtpSender dummyOTPSender() {
        return new DummyOtpSender();
    }

    @Bean
    @ConditionalOnProperty(value = "TWILIO_ACCOUNT_SID")
    public OtpSender twilioOTPSender() {
        return new TwilioOtpSender();
    }

    @Bean
    public OtpSender emailOtpSender(MailSender mailSender) {
        return new EmailOtpSender(mailSender);
    }

    @Bean
    public OtpGenerator otpGenerator() {
        return new OtpGenerator();
    }
}
