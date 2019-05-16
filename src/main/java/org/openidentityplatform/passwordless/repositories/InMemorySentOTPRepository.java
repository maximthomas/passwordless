package org.openidentityplatform.passwordless.repositories;

import lombok.extern.log4j.Log4j2;
import org.openidentityplatform.passwordless.models.SentOTP;

import java.util.HashMap;
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
