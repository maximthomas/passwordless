package org.openidentityplatform.passwordless.totp;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.junit.jupiter.api.Test;
import org.openidentityplatform.passwordless.totp.configuration.TotpConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TestTotpConfiguration {

    @Autowired
    TotpConfiguration totpConfiguration;
    @Autowired
    TimeBasedOneTimePasswordGenerator totpGenerator;
    @Test
    public void testConfiguration() {
        assertEquals("acme.com", totpConfiguration.getIssuer());
        assertNotNull(totpGenerator);
    }
}
