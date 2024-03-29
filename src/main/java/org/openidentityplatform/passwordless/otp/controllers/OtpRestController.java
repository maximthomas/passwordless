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

package org.openidentityplatform.passwordless.otp.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.openidentityplatform.passwordless.exceptions.NotFoundException;
import org.openidentityplatform.passwordless.otp.models.SendOtpRequest;
import org.openidentityplatform.passwordless.otp.models.SendOtpResult;
import org.openidentityplatform.passwordless.otp.models.VerifyOtpRequest;
import org.openidentityplatform.passwordless.otp.models.VerifyOtpResult;
import org.openidentityplatform.passwordless.otp.services.FrequentSendingForbidden;
import org.openidentityplatform.passwordless.otp.services.OtpService;
import org.openidentityplatform.passwordless.otp.services.OtpVerifyAttemptsExceeded;
import org.openidentityplatform.passwordless.otp.services.SendOtpException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/otp/v1")
public class OtpRestController {

    private final OtpService otpService;

    @PostMapping("/send")
    public SendOtpResult send(@RequestBody @Valid SendOtpRequest sendOTPRequest)
            throws NotFoundException, SendOtpException, FrequentSendingForbidden {
        return otpService.send(sendOTPRequest.getSender(), sendOTPRequest.getDestination());
    }

    @PostMapping("/verify")
    public VerifyOtpResult verify(@RequestBody @Valid VerifyOtpRequest verifyOTPRequest) throws NotFoundException, OtpVerifyAttemptsExceeded {
        return otpService.verify(verifyOTPRequest.sessionId, verifyOTPRequest.otp);
    }





}
