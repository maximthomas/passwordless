package org.openidentityplatform.passwordless.models;

import lombok.Data;
import lombok.ToString;
import org.openidentityplatform.passwordless.configuration.SpringContext;
import org.openidentityplatform.passwordless.services.OTPSender;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;


@Data
@Document
@ToString
public class OTPSetting {

    @Id
    private String id;

    @Indexed
    private String accountId;

    private String name;

    private String title;

    private String messageTemplate;

    private int otpLength;

    private boolean useLetters;

    private boolean useDigits;

    private long ttl; //OTP time to live

    private String sender;

    public OTPSender getOTPOtpSender() {

        return (OTPSender) SpringContext.getBean(sender);
    }

}
