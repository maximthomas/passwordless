package org.openidentityplatform.passwordless.otp.services;

public class SessionNotFoundException extends NotFoundException {

    public SessionNotFoundException() {
        super("session not found");
    }
}
