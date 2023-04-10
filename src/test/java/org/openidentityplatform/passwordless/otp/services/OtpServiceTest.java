package org.openidentityplatform.passwordless.otp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.openidentityplatform.passwordless.otp.configuration.OtpConfiguration;
import org.openidentityplatform.passwordless.otp.configuration.OtpSettings;
import org.openidentityplatform.passwordless.otp.models.SendOtpResult;
import org.openidentityplatform.passwordless.otp.models.SentOtp;
import org.openidentityplatform.passwordless.otp.models.VerifyOtpResult;
import org.openidentityplatform.passwordless.otp.repositories.SentOtpRepository;
import org.springframework.context.ApplicationContext;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class OtpServiceTest {

    private SentOtpRepository sentOtpRepository;

    private OtpSender otpSender;

    private final static String TYPE = "phone";

    private final static String PHONE = "+799912345678";
    private final static String OTP = "12345";
    private final static String SESSION_ID = UUID.randomUUID().toString();

    private final static int ATTEMPTS = 5;

    private OtpService otpService;

    private SentOtp sentOtp;
    @BeforeEach
    void setup() {

        otpSender = mock(OtpSender.class);

        OtpConfiguration otpConfiguration = mock(OtpConfiguration.class);

        OtpSettings otpSettings = new OtpSettings();
        otpSettings.setId("id");
        otpSettings.setName("OTPSettings");
        otpSettings.setSender(TYPE);
        otpSettings.setMessageTitle("One Time Password");
        otpSettings.setMessageTemplate("Confirmation code: ${otp}");

        when(otpConfiguration.getSetting(eq(TYPE))).thenReturn(otpSettings);
        when(otpConfiguration.getSetting(eq("unknown"))).thenThrow(new NoSuchElementException());
        when(otpConfiguration.getAttempts()).thenReturn(ATTEMPTS);
        when(otpConfiguration.getResendAllowedAfterMinutes()).thenReturn(1);

        sentOtp = new SentOtp();
        sentOtp.setSessionId(UUID.fromString(SESSION_ID));
        sentOtp.setDestination(PHONE);
        sentOtp.setOtp(OTP);
        sentOtp.setAttempts(5);
        sentOtp.setExpireTime(System.currentTimeMillis() + 1000);

        OtpGenerator otpGenerator = mock(OtpGenerator.class);
        when(otpGenerator.generateSentOTP(eq(otpSettings), eq(PHONE))).thenReturn(sentOtp);

        sentOtpRepository = mock(SentOtpRepository.class);
        when(sentOtpRepository.findById(eq(UUID.fromString(SESSION_ID)))).thenReturn(Optional.of(sentOtp));

        ApplicationContext applicationContext = mock(ApplicationContext.class);
        when(applicationContext.getBean(eq(TYPE), eq(OtpSender.class))).thenReturn(otpSender);

        otpService = new OtpService(otpConfiguration, sentOtpRepository, otpGenerator, applicationContext);

    }
    @Test
    void send() throws Exception {
        SendOtpResult result = otpService.send(TYPE, PHONE);
        assertNotNull(result.getSessionId());
        assertEquals(5, result.getRemainingAttempts());
        assertEquals(PHONE, result.getDestination());
        assertTrue(result.getResendAllowedAt() > System.currentTimeMillis());
    }

    @Test
    void send_typeNotFound() {
        assertThrows(SenderNotFoundException.class, () -> otpService.send("unknown", PHONE));
    }

    @Test
    void send_errorSendingOtp() throws SendOtpException {
        doThrow(SendOtpException.class).when(otpSender).sendOTP(anyString(), anyString(), anyString());
        assertThrows(SendOtpException.class, () -> otpService.send(TYPE, PHONE));
    }

    @Test
    void send_frequentSendForbidden() {
        SentOtp lastSentOtp = new SentOtp();
        lastSentOtp.setLastSentAt(System.currentTimeMillis() - 10000);
        when(sentOtpRepository.findFirstByDestinationOrderByLastSentAtDesc(PHONE)).thenReturn(Optional.of(lastSentOtp));
        assertThrows(FrequentSendingForbidden.class, () -> otpService.send(TYPE, PHONE));
    }

    @Test
    void verify() throws Exception {
        VerifyOtpResult result = otpService.verify(SESSION_ID, OTP);
        assertTrue(result.isValid());
        assertNull(result.getRemainingAttempts());
    }

    @Test
    void verify_sessionNotFound() {
        assertThrows(SessionNotFoundException.class, () -> otpService.verify("bad", PHONE));
    }

    @Test
    void verify_invalidOtp() throws Exception {
        VerifyOtpResult result = otpService.verify(SESSION_ID, "bad");
        assertFalse(result.isValid());
        assertEquals(ATTEMPTS - 1, result.getRemainingAttempts());
        Mockito.verify(sentOtpRepository, times(1)).save(eq(sentOtp));
    }

    @Test
    void verify_otpTimeout() {
        sentOtp.setExpireTime(System.currentTimeMillis() - 1000);
        assertThrows(SessionNotFoundException.class, () -> otpService.verify(SESSION_ID, PHONE));
    }

    @Test
    void verify_attemptsExceeded() {
        sentOtp.setAttempts(0);
        assertThrows(OtpVerifyAttemptsExceeded.class, () -> otpService.verify(SESSION_ID, PHONE));
    }
}