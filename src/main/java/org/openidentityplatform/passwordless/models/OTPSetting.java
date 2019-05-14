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
