/*
 * Copyright 2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openidentityplatform.passwordless.otp.repositories;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openidentityplatform.passwordless.otp.models.OTPSetting;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.List;

@Log4j2
public class FileBasedOTPSettingsRepository implements OTPSettingsRepository {


    @Value("${otp.settings.config:classpath:otp-settings.yaml}")
    private String configPath;

    private List<OTPSetting> otpSettings;

    private ResourceLoader resourceLoader;

    public FileBasedOTPSettingsRepository(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @PostConstruct
    private void init() {
        try {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            otpSettings = objectMapper.readValue(resourceLoader.getResource(configPath).getInputStream(), new TypeReference<List<OTPSetting>>(){});
            log.info("loaded OTP settings: {}", otpSettings);
        } catch (IOException e) {
            log.error("error occurred : {}", e.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public OTPSetting getSetting(String settingId) {
        return otpSettings.stream()
                .filter(o -> StringUtils.equals(settingId, o.getId()))
                .findFirst().orElse(null);

    }
}
