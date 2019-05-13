package org.openidentityplatform.passwordless.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {FileBasedOTPSettingsRepository.class, ObjectMapper.class})
public class FileBasedOTPSettingsRepositoryTest {


    @Autowired
    private FileBasedOTPSettingsRepository otpSettingsRepository;

    @Test
    public void getSetting() {
        assertNotNull(otpSettingsRepository.getSetting("sms"));
        assertNull(otpSettingsRepository.getSetting("not-exists"));
    }
}