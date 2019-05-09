package org.openidentityplatform.passwordless.configuration;

import org.openidentityplatform.passwordless.repositories.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
        return new InMemoryOTPSettingsRepository();
    }
}
