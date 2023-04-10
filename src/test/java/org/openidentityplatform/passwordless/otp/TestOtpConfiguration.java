package org.openidentityplatform.passwordless.otp;

import org.junit.jupiter.api.Test;
import org.openidentityplatform.passwordless.otp.configuration.OtpConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class TestOtpConfiguration {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void testConfiguration() {
        assertNotNull(applicationContext);
        OtpConfiguration otpConfiguration = applicationContext.getBean(OtpConfiguration.class);
        assertEquals(5, otpConfiguration.getAttempts());
    }
}
