package org.openidentityplatform.passwordless.services;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;
import org.openidentityplatform.passwordless.models.OTPSetting;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class DummyOTPSender implements OTPSender {

    @Override
    public void sendOTP(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties) {
        String message = createMessage(otpSetting, otp, destination, properties);
        log.info("message: {}", message);

    }
}
