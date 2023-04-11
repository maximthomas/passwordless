package org.openidentityplatform.passwordless.totp.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "totp")
@Getter
@Setter
public class TotpConfiguration {
    private String issuer;
    private String issuerLabel;

    private Integer digits = 6;

    private final String algorithm = "SHA1";

    private final Integer period = 30;
}
