package org.openidentityplatform.passwordless.services;

import lombok.extern.log4j.Log4j2;
import org.openidentityplatform.passwordless.models.OTPSetting;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.Map;

@Log4j2
public class EmailOTPSender implements OTPSender {

    private final MailSender mailSender;

    public EmailOTPSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendOTP(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties) {
        String messageStr = this.createMessage(otpSetting, otp, destination, properties);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(destination);
        msg.setText(messageStr);
        msg.setSubject(otpSetting.getMessageTitle());
        try{
            this.mailSender.send(msg);
        }
        catch (MailException ex) {
            log.error(ex);
        }
    }
}
