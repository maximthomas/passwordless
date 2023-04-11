package org.openidentityplatform.passwordless.totp.services;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openidentityplatform.passwordless.totp.configuration.TotpConfiguration;
import org.openidentityplatform.passwordless.totp.models.RegisteredTotp;
import org.openidentityplatform.passwordless.totp.repository.RegisteredTotpRepository;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TotpServiceTest {

    TotpService totpService;

    final static String USERNAME = "John";

    final static String SECRET = "ONSWG4TFOQ======";

    RegisteredTotpRepository totpRepository;

    TotpConfiguration totpConfiguration;

    TimeBasedOneTimePasswordGenerator generator = new TimeBasedOneTimePasswordGenerator();
    @BeforeEach
    void setup() {
        totpConfiguration = new TotpConfiguration();
        totpConfiguration.setIssuer("acme.com");
        totpConfiguration.setIssuerLabel("Acme LLC");
        totpRepository = mock(RegisteredTotpRepository.class);
        totpService = new TotpService(totpRepository, generator, totpConfiguration);
    }

    @Test
    void testRegister() {
        when(totpRepository.findById(USERNAME)).thenReturn(Optional.empty());
        URI uri = totpService.register(USERNAME);
        assertEquals("otpauth", uri.getScheme());
        assertEquals("totp", uri.getHost());
        List<NameValuePair> params = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
        assertTrue(params.stream().anyMatch(p -> p.getName().equals("secret")));
        assertTrue(params.stream().anyMatch(p -> p.getName().equals("issuer")));
        verify(totpRepository, times(1)).save(any(RegisteredTotp.class));
    }

    @Test
    void testRegister_userExists() {
        when(totpRepository.existsById(USERNAME)).thenReturn(true);

        RegisteredTotp registeredTotp = new RegisteredTotp();
        registeredTotp.setUsername(USERNAME);
        registeredTotp.setSecret(SECRET);

        when(totpRepository.findById(USERNAME)).thenReturn(Optional.of(registeredTotp));
        URI uri = totpService.register(USERNAME);
        assertEquals("otpauth", uri.getScheme());
        assertEquals("totp", uri.getHost());
        List<NameValuePair> params = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);
        assertTrue(params.stream().anyMatch(p -> p.getName().equals("secret")));
        assertTrue(params.stream().anyMatch(p -> p.getName().equals("issuer")));
        verify(totpRepository, times(1)).findById(eq(USERNAME));
        verify(totpRepository, times(1)).save(any(RegisteredTotp.class));
    }

    @Test
    void testVerify() throws Exception {
        RegisteredTotp registeredTotp = new RegisteredTotp();
        registeredTotp.setUsername(USERNAME);
        registeredTotp.setSecret(SECRET);
        when(totpRepository.findById(USERNAME)).thenReturn(Optional.of(registeredTotp));
        Key key = totpService.restoreKey(SECRET);
        int totp = generator.generateOneTimePassword(key, Instant.now());
        boolean valid = totpService.verify(USERNAME, totp);
        assertTrue(valid);
    }

    @Test
    void testVerify_invalidCode() throws UserNotFoundException {
        RegisteredTotp registeredTotp = new RegisteredTotp();
        registeredTotp.setUsername(USERNAME);
        registeredTotp.setSecret(SECRET);
        when(totpRepository.findById(USERNAME)).thenReturn(Optional.of(registeredTotp));
        boolean valid = totpService.verify(USERNAME, 1);
        assertFalse(valid);
    }

    @Test
    void testVerify_userNotFound() {
        when(totpRepository.findById(USERNAME)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> totpService.verify(USERNAME, 1));
    }
}