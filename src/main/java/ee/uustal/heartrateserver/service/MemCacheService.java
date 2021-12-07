package ee.uustal.heartrateserver.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemCacheService {

    private static final int MAX_AMOUNT_OF_LATEST_HEART_RATES = 5000;
    private final Map<Long, Integer> storage;

    public MemCacheService() {
        this.storage = Collections.synchronizedMap(new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
                return size() > MAX_AMOUNT_OF_LATEST_HEART_RATES;
            }
        });
    }

    public void addHeartRate(Integer heartRate) {
        storage.putIfAbsent(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+02:00")), heartRate);
    }

    public double getAverage(int minutes) {
        final List<Integer> heartRates = storage.entrySet().stream()
                .filter(entry -> entry.getKey() > LocalDateTime.now().minusMinutes(minutes).toEpochSecond(ZoneOffset.of("+02:00")))
                .map(Map.Entry::getValue)
                .toList();

        return heartRates.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }
}
