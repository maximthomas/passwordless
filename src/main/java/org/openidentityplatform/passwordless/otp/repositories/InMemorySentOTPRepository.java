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

import lombok.extern.log4j.Log4j2;
import org.openidentityplatform.passwordless.otp.models.SentOTP;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Log4j2
public class InMemorySentOTPRepository implements SentOTPRepository {

    private Map<String, SentOTP> sentOTPMap = new ConcurrentHashMap<>();

    @Override
    public void save(SentOTP sentOTP) {
        sentOTPMap.put(sentOTP.getOperationId(), sentOTP);
    }

    @Override
    public SentOTP getById(String operationId) {
        return sentOTPMap.get(operationId);
    }

    private ScheduledExecutorService cleanup = Executors.newScheduledThreadPool(1);

    public InMemorySentOTPRepository() {
        cleanup.scheduleAtFixedRate(() -> {
            log.info("start cleanup expired OTPs");
            Set<String> expiredOperationIds = sentOTPMap.entrySet().stream()
                    .filter(v -> v.getValue().getExpireTime() < System.currentTimeMillis())
                    .map(Map.Entry::getKey).collect(Collectors.toSet());


            expiredOperationIds.forEach(expiredOperationId -> sentOTPMap.remove(expiredOperationId));
            log.info("removed expired operation Ids {}", expiredOperationIds);
        },  5, 5, TimeUnit.MINUTES);
    }
}
