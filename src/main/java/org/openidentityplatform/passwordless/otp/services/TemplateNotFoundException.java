package org.openidentityplatform.passwordless.otp.services;

import org.openidentityplatform.passwordless.exceptions.NotFoundException;

public class TemplateNotFoundException extends NotFoundException {
    public TemplateNotFoundException() {
        super("template not found");
    }
}
