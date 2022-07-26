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

import lombok.extern.log4j.Log4j2;
import org.openidentityplatform.passwordless.otp.configuration.OTPSetting;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
public class DummyOtpSender implements OtpSender {

    @Override
    public void sendOTP(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties) {
        String message = createMessage(otpSetting, otp, destination, properties);
        log.info("message: {}", message);

    }
}
