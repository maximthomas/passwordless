package org.openidentityplatform.passwordless.totp.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QrServiceTest {

    @Test
    void testGenerateQr() {
        QrService qrService = new QrService();
        String qr = qrService.generateQr("test");
        assertNotNull(qr);
    }
}