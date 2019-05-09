package org.openidentityplatform.passwordless.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class OTPSetting {

    @Id
    private String id;

    @Indexed
    private String accountId;

    private String name;

    private String messageTemplate;

    private int otpLength;

    private boolean useLetters;

    private boolean useDigits;

    private long ttl; //OTP time to live

}
