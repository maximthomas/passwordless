package org.openidentityplatform.passwordless.otp.services;

public class TemplateNotFoundException extends NotFoundException {
    public TemplateNotFoundException() {
        super("template not found");
    }
}
