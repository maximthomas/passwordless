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

import org.openidentityplatform.passwordless.configuration.SpringContext;
import org.openidentityplatform.passwordless.otp.repositories.*;
import org.openidentityplatform.passwordless.otp.services.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSender;

@Configuration
@Import({OTPSettingsList.class, SpringContext.class})
public class OTPConfiguration {
    @Bean
    public AccountRepository accountRepository() {
        return new InMemoryAccountRepository(); //TODO add persistence
    }

    @Bean
    public SentOTPRepository sentOTPRepository() {
        return new InMemorySentOTPRepository(); //TODO add persistence
    }

    @Bean
    public OTPSender dummyOTPSender() {
        return new DummyOTPSender();
    }

    @Bean
    @ConditionalOnProperty(value = "TWILIO_ACCOUNT_SID")
    public OTPSender twilioOTPSender() {
        return new TwilioOTPSender();
    }

    @Bean
    public OTPSender emailOTPSender(MailSender mailSender) {
        return new EmailOTPSender(mailSender);
    }

    @Bean
    public OTPGenerator otpGenerator() {
        return new OTPGenerator();
    }
}
