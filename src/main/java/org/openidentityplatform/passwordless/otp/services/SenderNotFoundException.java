package org.openidentityplatform.passwordless.otp.services;

import org.openidentityplatform.passwordless.exceptions.NotFoundException;

public class SenderNotFoundException extends NotFoundException {

    public SenderNotFoundException() {
        super("sender not found");
    }
}
