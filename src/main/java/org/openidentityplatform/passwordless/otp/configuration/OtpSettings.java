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

package org.openidentityplatform.passwordless.otp.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.openidentityplatform.passwordless.otp.services.OtpSender;
import org.springframework.context.ApplicationContext;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OtpSettings {

    private String id;

    private String accountId;

    private String name;

    private String messageTitle;

    private String messageTemplate;

    private int otpLength;

    private boolean useLetters;

    private boolean useDigits;

    private long ttlMinutes; //OTP time to live

    private String sender;

    public OtpSender getOtpSender(ApplicationContext applicationContext) {
        return applicationContext.getBean(sender, OtpSender.class);
    }

}
