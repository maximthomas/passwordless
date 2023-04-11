package org.openidentityplatform.passwordless.totp.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.openidentityplatform.passwordless.totp.models.RegistrationRequest;
import org.openidentityplatform.passwordless.totp.models.RegistrationResponse;
import org.openidentityplatform.passwordless.totp.models.VerificationRequest;
import org.openidentityplatform.passwordless.totp.models.VerificationResponse;
import org.openidentityplatform.passwordless.totp.services.QrService;
import org.openidentityplatform.passwordless.totp.services.TotpService;
import org.openidentityplatform.passwordless.totp.services.UserNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/totp/v1")
public class TotpRestController {

    private TotpService totpService;

    private QrService qrService;

    @PostMapping("/register")
    public RegistrationResponse register(@RequestBody @Valid RegistrationRequest registrationRequest) {
        final URI uri = totpService.register(registrationRequest.getUsername());
        final String qr = qrService.generateQr(uri.toString());
        final RegistrationResponse registrationResponse = new RegistrationResponse();
        registrationResponse.setUri(uri.toString());
        registrationResponse.setQr(qr);
        return registrationResponse;
    }

    @PostMapping("/verify")
    public VerificationResponse verify(@RequestBody @Valid VerificationRequest verificationRequest) throws UserNotFoundException {
        boolean valid = totpService.verify(verificationRequest.getUsername(), verificationRequest.getTotp());
        return new VerificationResponse(valid);
    }
}
