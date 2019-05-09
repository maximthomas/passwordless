package org.openidentityplatform.passwordless.services;

import org.openidentityplatform.passwordless.models.OTPSetting;

import java.util.Map;

public interface OTPSender {
    void sendOTP(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties);
}
