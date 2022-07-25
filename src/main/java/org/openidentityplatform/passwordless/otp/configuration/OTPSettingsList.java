package org.openidentityplatform.passwordless.otp.configuration;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.openidentityplatform.passwordless.otp.services.OTPSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "otp")
@Getter
@Setter
public class OTPSettingsList {

    private List<OTPSetting> settings;

    public OTPSetting getSetting(String settingId) {
        return settings.stream()
                .filter(s -> s.getId().equals(settingId))
                .findFirst()
                .orElseThrow();
    }
}
