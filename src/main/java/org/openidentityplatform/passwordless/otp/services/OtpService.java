package org.openidentityplatform.passwordless.otp.services;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.text.StringSubstitutor;
import org.openidentityplatform.passwordless.otp.configuration.OtpSettings;
import org.openidentityplatform.passwordless.otp.configuration.OtpConfiguration;
import org.openidentityplatform.passwordless.otp.models.SendOtpResult;
import org.openidentityplatform.passwordless.otp.models.SentOtp;
import org.openidentityplatform.passwordless.otp.models.VerifyOtpResult;
import org.openidentityplatform.passwordless.otp.repositories.SentOtpRepository;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Log4j2
public class OtpService {

    private final OtpConfiguration otpConfiguration;
    private final SentOtpRepository sentOtpRepository;
    private final OtpGenerator otpGenerator;
    private final ApplicationContext applicationContext;

    public SendOtpResult send(String type, String destination) throws NotFoundException, SendOtpException, FrequentSendingForbidden {
        final OtpSettings otpSettings;
        try {
            otpSettings = otpConfiguration.getSetting(type);
            if(otpSettings == null) {
                throw new NoSuchElementException();
            }
        } catch (NoSuchElementException e) {
            log.warn("sender {} not found", type);
            throw new SenderNotFoundException();
        }
        final SentOtp sentOTP = otpGenerator.generateSentOTP(otpSettings, destination);
        final OtpSender otpSender;
        try {
            otpSender = otpSettings.getOtpSender(applicationContext);
        } catch (BeansException e) {
            log.warn("otp sender {} not found", otpSettings.getSender());
            throw new SenderNotFoundException();
        }
        final String messageTitle = otpSettings.getMessageTitle();
        final String messageBody = createMessage(otpSettings.getMessageTemplate(), sentOTP.getOtp());

        validateFrequentSending(destination);

        otpSender.sendOTP(destination, messageTitle, messageBody);
        sentOTP.setAttempts(otpConfiguration.getAttempts());
        sentOtpRepository.save(sentOTP);

        return new SendOtpResult(sentOTP.getSessionId().toString(), sentOTP.getDestination(), sentOTP.getExpireTime(), sentOTP.getAttempts());
    }

    private void validateFrequentSending(String destination) throws FrequentSendingForbidden {
        if(otpConfiguration.getResendAllowedAfterMinutes() == null
                || otpConfiguration.getResendAllowedAfterMinutes() == 0) {
            return;
        }
        Optional<SentOtp> sentOtp = sentOtpRepository.findFirstByDestinationOrderByLastSentAtDesc(destination);
        if(sentOtp.isEmpty()) {
            return;
        }
        int resendAfterMilliseconds = otpConfiguration.getResendAllowedAfterMinutes() * 1000 * 60;
        long resendAllowedAt = sentOtp.get().getLastSentAt() + resendAfterMilliseconds;
        if(resendAllowedAt > System.currentTimeMillis()) {
            log.warn("frequent sending to {} forbidden", destination);
            throw new FrequentSendingForbidden();
        }
    }

    private String createMessage(String messageTemplate, String otp) {
        final Map<String, String> values = new HashMap<>();
        values.put("otp", otp);
        StringSubstitutor sub = new StringSubstitutor(values);
        return sub.replace(messageTemplate);
    }

    public VerifyOtpResult verify(String sessionId, String otp) throws NotFoundException, OtpVerifyAttemptsExceeded {
        final UUID sessionUUID;
        try {
            sessionUUID = UUID.fromString(sessionId);
        } catch (IllegalArgumentException e) {
            log.warn("session {} not found", sessionId);
            throw new SessionNotFoundException();
        }

        Optional<SentOtp> sentOtpOptional = sentOtpRepository.findById(sessionUUID);
        if(sentOtpOptional.isEmpty()) {
            log.warn("session {} not found", sessionId);
            throw new SessionNotFoundException();
        }

        SentOtp sentOtp = sentOtpOptional.get();

        if(sentOtp.getExpireTime() < System.currentTimeMillis()) {
            log.warn("session {} expired", sessionId);
            throw new SessionNotFoundException();
        }

        if(sentOtp.getAttempts() == 0) {
            throw new OtpVerifyAttemptsExceeded();
        }

        boolean result = sentOtp.getExpireTime() > System.currentTimeMillis()
                && sentOtp.getOtp().equals(otp);

        Integer remainingAttempts = null;
        if(!result) {
            remainingAttempts = sentOtp.getAttempts() - 1;
            remainingAttempts = remainingAttempts < 0 ? 0 : remainingAttempts;
            sentOtp.setAttempts(remainingAttempts);
            sentOtpRepository.save(sentOtp);
        }

        return new VerifyOtpResult(result, remainingAttempts);
    }
}
