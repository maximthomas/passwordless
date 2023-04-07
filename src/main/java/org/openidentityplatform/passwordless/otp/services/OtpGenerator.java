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

import org.apache.commons.lang3.RandomStringUtils;
import org.openidentityplatform.passwordless.otp.configuration.OtpSettings;
import org.openidentityplatform.passwordless.otp.models.SentOtp;

import java.util.UUID;

public class OtpGenerator {

    public SentOtp generateSentOTP(OtpSettings otpSettings, String destination) {
        String otp = RandomStringUtils.random(otpSettings.getOtpLength(), otpSettings.isUseLetters(), otpSettings.isUseDigits());
        SentOtp sentOTP = new SentOtp();
        sentOTP.setSessionId(UUID.randomUUID());
        sentOTP.setExpireTime(System.currentTimeMillis() + otpSettings.getTtlMinutes() * 60 * 1000);
        sentOTP.setOtp(otp);
        sentOTP.setDestination(destination);
        return sentOTP;
    }
}
