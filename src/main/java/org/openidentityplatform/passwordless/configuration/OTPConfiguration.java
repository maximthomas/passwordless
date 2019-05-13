package org.openidentityplatform.passwordless.configuration;

import org.openidentityplatform.passwordless.repositories.*;
import org.openidentityplatform.passwordless.services.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.mail.MailSender;

@Configuration
@Import(SpringContext.class)
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
    public OTPSettingsRepository otpSettingsRepository() {
        return new FileBasedOTPSettingsRepository();
    }

    @Bean
    public OTPSender dummyOtpSender() {
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
