package org.openidentityplatform.passwordless.repositories;

import org.openidentityplatform.passwordless.models.OTPSetting;

public interface OTPSettingsRepository {
    OTPSetting getSetting(String settingId);
}
