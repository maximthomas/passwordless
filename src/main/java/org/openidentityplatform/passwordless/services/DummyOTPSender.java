package org.openidentityplatform.passwordless.services;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;
import org.openidentityplatform.passwordless.models.OTPSetting;

import java.util.HashMap;
import java.util.Map;

@Log4j2
public class DummyOTPSender implements OTPSender {

    @Override
    public void sendOTP(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties) {
        String messageTemplate = otpSetting.getMessageTemplate();

        Map<String, String> values = new HashMap<>();
        values.putAll(properties);
        values.put("destination", destination);
        values.put("otp", otp);
        StringSubstitutor sub = new StringSubstitutor(values);
        String message = sub.replace(messageTemplate);

        log.info("message: {}", message);

    }
}
