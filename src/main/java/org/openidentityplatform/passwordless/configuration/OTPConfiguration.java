package org.openidentityplatform.passwordless.configuration;

import org.openidentityplatform.passwordless.repositories.*;
import org.openidentityplatform.passwordless.services.DummyOTPSender;
import org.openidentityplatform.passwordless.services.OTPGenerator;
import org.openidentityplatform.passwordless.services.OTPSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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

    @Bean(name = "dummyOtpSender")
    public OTPSender otpSender() {
        return new DummyOTPSender();
    }

    @Bean
    public OTPGenerator otpGenerator() {
        return new OTPGenerator();
    }
}
