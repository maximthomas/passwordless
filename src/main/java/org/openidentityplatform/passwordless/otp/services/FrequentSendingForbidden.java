package org.openidentityplatform.passwordless.otp.services;

public class FrequentSendingForbidden extends BadRequestException {
    public FrequentSendingForbidden() {
        super("Frequent OTP send forbidden");
    }
}
