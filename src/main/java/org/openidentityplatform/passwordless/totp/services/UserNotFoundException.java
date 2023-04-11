package org.openidentityplatform.passwordless.totp.services;

import org.openidentityplatform.passwordless.exceptions.NotFoundException;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException() {
        super("User not found");
    }
}
