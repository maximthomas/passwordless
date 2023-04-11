package org.openidentityplatform.passwordless.totp.configuration;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class TotpBeansConfiguration {
    @Bean(name = "totpGenerator")
    public TimeBasedOneTimePasswordGenerator totpGenerator(TotpConfiguration totpConfiguration) {
        final Duration duration = Duration.ofSeconds(totpConfiguration.getPeriod());
        final Integer length = totpConfiguration.getDigits();
        final String algorithm = "Hmac".concat(totpConfiguration.getAlgorithm());
        return new TimeBasedOneTimePasswordGenerator(duration, length, algorithm);
    }
}
