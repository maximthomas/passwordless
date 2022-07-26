package org.openidentityplatform.passwordless.otp.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.openidentityplatform.passwordless.otp.configuration.OTPSetting;
import org.openidentityplatform.passwordless.otp.configuration.OTPSettingsList;
import org.openidentityplatform.passwordless.otp.models.SendOTPResult;
import org.openidentityplatform.passwordless.otp.models.SentOtp;
import org.openidentityplatform.passwordless.otp.models.VerifyOTPResult;
import org.openidentityplatform.passwordless.otp.repositories.SentOtpRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class OtpService {

    private final OTPSettingsList otpSettingsList;
    private final SentOtpRepository sentOtpRepository;
    private final OtpGenerator otpGenerator;
    private final ApplicationContext applicationContext;

    public SendOTPResult send(String settingId, String destination, Map<String, String> properties) throws SenderNotFoundException {
        OTPSetting otpSetting = otpSettingsList.getSetting(settingId);
        SentOtp sentOTP = otpGenerator.generateSentOTP(otpSetting, destination);

        OtpSender otpSender;
        try {
            otpSender = applicationContext.getBean(otpSetting.getSender(), OtpSender.class);
        }catch (BeansException e) {
            log.warn("otp sender {} not found", otpSetting.getSender());
            throw new SenderNotFoundException();
        }
        otpSender.sendOTP(otpSetting, sentOTP.getOtp(), destination, properties);
        sentOtpRepository.save(sentOTP);
        return new SendOTPResult(sentOTP.getOperationId().toString());
    }

    public VerifyOTPResult verify(String operationId, String otp) throws OperationNotFoundException {
        Optional<SentOtp> sentOtpOptional = sentOtpRepository.findById(operationId);
        if(sentOtpOptional.isEmpty()) {
            throw new OperationNotFoundException();
        }
        SentOtp sentOtp = sentOtpOptional.get();

        boolean result = sentOtp.getExpireTime() > System.currentTimeMillis()
                && sentOtp.getOtp().equals(otp);

        return new VerifyOTPResult(result, sentOtp.getDestination());
    }
}
