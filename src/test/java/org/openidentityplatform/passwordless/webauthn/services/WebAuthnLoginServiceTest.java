package org.openidentityplatform.passwordless.webauthn.services;

import org.junit.jupiter.api.Test;
import org.openidentityplatform.passwordless.webauthn.configuration.WebAuthnConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.HashSet;

import static org.mockito.Mockito.mock;

class WebAuthnLoginServiceTest {

    @Test
    public void test() {
        WebAuthnConfiguration webAuthnConfiguration = mock(WebAuthnConfiguration.class);
        WebAuthnLoginService webAuthnLoginService = new WebAuthnLoginService(webAuthnConfiguration);
        MockHttpServletRequest request = new MockHttpServletRequest();
        webAuthnLoginService.requestCredentials("", request, new HashSet<>());
    }

}