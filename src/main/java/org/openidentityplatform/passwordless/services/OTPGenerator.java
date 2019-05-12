package org.openidentityplatform.passwordless.services;

import org.apache.commons.lang3.RandomStringUtils;
import org.openidentityplatform.passwordless.models.OTPSetting;
import org.openidentityplatform.passwordless.models.SentOTP;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OTPGenerator {

    public SentOTP generateSentOTP(OTPSetting otpSetting) {
        String OTP = RandomStringUtils.random(otpSetting.getOtpLength(), otpSetting.isUseLetters(), otpSetting.isUseDigits());
        SentOTP sentOTP = new SentOTP();
        sentOTP.setOperationId(UUID.randomUUID().toString());
        sentOTP.setExpireTime(System.currentTimeMillis() + otpSetting.getTtl() * 60 * 1000);
        sentOTP.setOTP(OTP);
        return sentOTP;
    }
}
