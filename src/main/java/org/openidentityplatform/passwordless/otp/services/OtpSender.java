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

package org.openidentityplatform.passwordless.otp.services;

import org.apache.commons.text.StringSubstitutor;
import org.openidentityplatform.passwordless.otp.configuration.OTPSetting;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public interface OtpSender {
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
