package org.openidentityplatform.passwordless.otp.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "otp")
@Getter
@Setter
public class OtpConfiguration {

    private Integer attempts;

    private Integer resendAllowedAfterMinutes;
    private List<OtpSettings> settings;
    public OtpSettings getSetting(String settingId) {
        return settings.stream()
                .filter(s -> s.getId().equals(settingId))
                .findFirst()
                .orElseThrow();
    }
}
