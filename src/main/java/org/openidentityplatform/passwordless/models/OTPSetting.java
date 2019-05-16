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

package org.openidentityplatform.passwordless.models;

import lombok.Data;
import lombok.ToString;
import org.openidentityplatform.passwordless.configuration.SpringContext;
import org.openidentityplatform.passwordless.services.OTPSender;

@Data
@ToString
public class OTPSetting {

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

    public OTPSender getOTPOtpSender() {

        return (OTPSender) SpringContext.getBean(sender);
    }

}
