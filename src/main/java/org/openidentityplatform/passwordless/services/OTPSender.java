package org.openidentityplatform.passwordless.services;

import org.apache.commons.text.StringSubstitutor;
import org.openidentityplatform.passwordless.models.OTPSetting;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public interface OTPSender {
    void sendOTP(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties);

    default String createMessage(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties) {
        String messageTemplate = otpSetting.getMessageTemplate();

        Map<String, String> values = new HashMap<>();
        if(!CollectionUtils.isEmpty(properties)) {
            values.putAll(properties);
        }
        values.put("destination", destination);
        values.put("otp", otp);
        StringSubstitutor sub = new StringSubstitutor(values);
        return  sub.replace(messageTemplate);
    }
}
