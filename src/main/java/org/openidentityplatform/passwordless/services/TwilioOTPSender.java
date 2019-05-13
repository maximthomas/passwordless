package org.openidentityplatform.passwordless.services;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.log4j.Log4j2;
import org.openidentityplatform.passwordless.models.OTPSetting;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Map;

@Log4j2
public class TwilioOTPSender implements OTPSender {

    @Value("#{environment.TWILIO_MESSAGING_SERVICE_SID}")
    private String MESSAGING_SERVICE_SID;

    @Value("#{environment.TWILIO_ACCOUNT_SID}")
    private String ACCOUNT_SID;

    @Value("#{environment.TWILIO_AUTH_TOKEN}")
    private String AUTH_TOKEN;

    @PostConstruct
    private void init() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    @Override
    public void sendOTP(OTPSetting otpSetting, String otp, String destination, Map<String, String> properties) {
        String messageBody = createMessage(otpSetting, otp, destination, properties);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(destination),
                MESSAGING_SERVICE_SID,
                messageBody)
                .create();
        log.info("sent message {}",message.getSid());
    }
}
