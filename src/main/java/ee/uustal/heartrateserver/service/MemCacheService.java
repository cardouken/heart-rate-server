package ee.uustal.heartrateserver.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class MemCacheService {

    private static final int MAX_AMOUNT_OF_LATEST_HEART_RATES = 3000;
    private static final ZoneOffset UTC_OFFSET = ZoneOffset.of("+02:00");
    private final Map<Long, Integer> storage;

    public MemCacheService() {
        this.storage = Collections.synchronizedMap(new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Integer> eldest) {
                // get newest entry
                return size() > MAX_AMOUNT_OF_LATEST_HEART_RATES;
            }
        });
    }

    public void addHeartRate(Integer heartRate) {
        storage.putIfAbsent(LocalDateTime.now().toEpochSecond(UTC_OFFSET), heartRate);
    }

    public double getAverage(int minutes) {

        final List<Integer> heartRates = storage.entrySet().stream()
                .filter(entry -> entry.getKey() > LocalDateTime.now().minusMinutes(minutes).toEpochSecond(UTC_OFFSET))
                .map(Map.Entry::getValue)
                .toList();

        final double average = heartRates.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);

        return Math.round(average);
    }
}
