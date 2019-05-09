package org.openidentityplatform.passwordless.controlles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openidentityplatform.passwordless.models.OTPSetting;
import org.openidentityplatform.passwordless.models.SentOTP;
import org.openidentityplatform.passwordless.repositories.OTPSettingsRepository;
import org.openidentityplatform.passwordless.repositories.SentOTPRepository;
import org.openidentityplatform.passwordless.services.OTPGenerator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/otp/v1")
public class OTPRestController {

    private OTPSettingsRepository otpSettingsRepository;

    private SentOTPRepository sentOTPRepository;

    private OTPGenerator otpGenerator;

    public OTPRestController(OTPSettingsRepository otpSettingsRepository,
                             SentOTPRepository sentOTPRepository,
                             OTPGenerator otpGenerator) {
        this.otpSettingsRepository = otpSettingsRepository;
        this.sentOTPRepository = sentOTPRepository;
        this.otpGenerator = otpGenerator;
    }

    @PostMapping("/{settingId}/send")
    public SendOTPResult send(@PathVariable("settingId") String settingId, SendOTP sendOTP) {
        OTPSetting otpSetting = otpSettingsRepository.getSetting(settingId);
        SentOTP sentOTP = otpGenerator.generateSentOTP(otpSetting);

        return new SendOTPResult(sentOTP.getOperationId());
    }

    @Data
    public static class SendOTP {
        private String destination;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendOTPResult {
        private String operationId;
    }

}
