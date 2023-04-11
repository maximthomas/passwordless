package org.openidentityplatform.passwordless.otp.services;

import org.openidentityplatform.passwordless.exceptions.NotFoundException;

public class SessionNotFoundException extends NotFoundException {

    public SessionNotFoundException() {
        super("session not found");
    }
}
