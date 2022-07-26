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

import lombok.AllArgsConstructor;
import org.openidentityplatform.passwordless.otp.models.SendOTPRequest;
import org.openidentityplatform.passwordless.otp.models.SendOTPResult;
import org.openidentityplatform.passwordless.otp.models.VerifyOTPRequest;
import org.openidentityplatform.passwordless.otp.models.VerifyOTPResult;
import org.openidentityplatform.passwordless.otp.services.OperationNotFoundException;
import org.openidentityplatform.passwordless.otp.services.OtpService;
import org.openidentityplatform.passwordless.otp.services.SenderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/otp/v1")
public class OtpRestController {

    private final OtpService otpService;

    @PostMapping("/{settingId}/send")
    public SendOTPResult send(@PathVariable("settingId") String settingId, @RequestBody SendOTPRequest sendOTPRequest) throws SenderNotFoundException {
        return otpService.send(settingId, sendOTPRequest.getDestination(), sendOTPRequest.getProperties());
    }

    @PostMapping("/verify")
    public VerifyOTPResult verify(@RequestBody VerifyOTPRequest verifyOTPRequest) throws OperationNotFoundException {
        return otpService.verify(verifyOTPRequest.getOperationId(), verifyOTPRequest.getOtp());
    }

    @ExceptionHandler(OperationNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleOperationNotFoundException() {
        return new ResponseEntity<>(Collections.singletonMap("error", "operation not found"), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SenderNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleSenderNotFoundException() {
        return new ResponseEntity<>(Collections.singletonMap("error", "sender not found"), HttpStatus.NOT_FOUND);
    }


}
