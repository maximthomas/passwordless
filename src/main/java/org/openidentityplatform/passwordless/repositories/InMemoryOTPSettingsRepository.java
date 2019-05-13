package org.openidentityplatform.passwordless.repositories;

import org.openidentityplatform.passwordless.models.OTPSetting;

import java.util.HashMap;
import java.util.Map;

public class InMemoryOTPSettingsRepository implements OTPSettingsRepository {

    private Map<String, OTPSetting> otpSettingMap;

    public InMemoryOTPSettingsRepository() {
        otpSettingMap = new HashMap<>();
        OTPSetting smsSetting = new OTPSetting();
        smsSetting.setId("sms");
        smsSetting.setAccountId(null);
        smsSetting.setMessageTemplate("Code: ${otp}");
        smsSetting.setName("SMS sender");
        smsSetting.setOtpLength(6);
        smsSetting.setUseDigits(true);
        smsSetting.setUseLetters(false);
        smsSetting.setTtlMinutes(3);
        smsSetting.setSender("dummyOtpSender");

        otpSettingMap.put(smsSetting.getId(), smsSetting);
    }


    @Override
    public OTPSetting getSetting(String settingId) {
        return otpSettingMap.get(settingId);
    }
}
