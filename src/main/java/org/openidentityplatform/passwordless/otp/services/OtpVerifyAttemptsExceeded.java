package org.openidentityplatform.passwordless.otp.services;

public class OtpVerifyAttemptsExceeded extends BadRequestException {
    public OtpVerifyAttemptsExceeded() {
        super("OTP verify attempts exceeded");
    }
}
