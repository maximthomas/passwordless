package org.openidentityplatform.passwordless.otp.services;

public class BadRequestException extends Exception {
    public BadRequestException(String message) {
        super(message);
    }
}
