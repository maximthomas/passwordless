package org.openidentityplatform.passwordless.otp.services;

public class SendOtpException extends Exception {
    public SendOtpException() {
        super("exception ocurred while sending OTP");
    }
}
