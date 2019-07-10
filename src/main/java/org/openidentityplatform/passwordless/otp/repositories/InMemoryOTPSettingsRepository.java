/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openidentityplatform.passwordless.otp.repositories;

import org.openidentityplatform.passwordless.otp.models.OTPSetting;

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
