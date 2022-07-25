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
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openidentityplatform.passwordless.otp.configuration.OTPSetting;
import org.openidentityplatform.passwordless.otp.models.SentOTP;
import org.openidentityplatform.passwordless.otp.configuration.OTPSettingsList;
import org.openidentityplatform.passwordless.otp.repositories.SentOTPRepository;
import org.openidentityplatform.passwordless.otp.services.OTPGenerator;
import org.openidentityplatform.passwordless.otp.services.OTPSender;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/otp/v1")
public class OTPRestController {

    private final OTPSettingsList otpSettingsList;

    private final SentOTPRepository sentOTPRepository;

    private final OTPGenerator otpGenerator;


    public OTPRestController(OTPSettingsList otpSettingsList,
                             SentOTPRepository sentOTPRepository,
                             OTPGenerator otpGenerator) {
        this.otpSettingsList = otpSettingsList;
        this.sentOTPRepository = sentOTPRepository;
        this.otpGenerator = otpGenerator;

    }

    @PostMapping("/{settingId}/send")
    public SendOTPResult send(@PathVariable("settingId") String settingId, @RequestBody SendOTPRequest sendOTPRequest) {
        OTPSetting otpSetting = otpSettingsList.getSetting(settingId);
        SentOTP sentOTP = otpGenerator.generateSentOTP(otpSetting);
        OTPSender otpSender = otpSetting.getOtpSender();
        otpSender.sendOTP(otpSetting, sentOTP.getOTP(), sendOTPRequest.getDestination(), sendOTPRequest.getProperties());
        sentOTPRepository.save(sentOTP);
        return new SendOTPResult(sentOTP.getOperationId());
    }

    @PostMapping("/verify")
    public VerifyOTPResult verify(@RequestBody VerifyOTPRequest verifyOTPRequest) {
        SentOTP sentOTP = sentOTPRepository.getById(verifyOTPRequest.getOperationId());
        if(sentOTP == null) {
            throw new OperationNotFoundException();
        }

        boolean result = sentOTP.getExpireTime() > System.currentTimeMillis()
                && sentOTP.getOTP().equals(verifyOTPRequest.getOtp());

        return new VerifyOTPResult(result);
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Opertaion not found")
    public static class OperationNotFoundException extends RuntimeException {

    }

    @Data
    public static class SendOTPRequest {
        private String destination;
        private Map<String, String> properties;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendOTPResult {
        private String operationId;
    }

    @Data
    public static class VerifyOTPRequest {
        public String operationId;
        public String otp;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static  class VerifyOTPResult {
        private boolean verified;
    }

}
