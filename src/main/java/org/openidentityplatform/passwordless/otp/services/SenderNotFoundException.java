package org.openidentityplatform.passwordless.otp.services;

public class SenderNotFoundException extends NotFoundException {

    public SenderNotFoundException() {
        super("sender not found");
    }
}
