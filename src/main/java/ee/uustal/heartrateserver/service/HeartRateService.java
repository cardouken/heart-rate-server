package ee.uustal.heartrateserver.service;

import ee.uustal.heartrateserver.config.JsonUtility;
import ee.uustal.heartrateserver.controller.api.request.HeartRateRequest;
import ee.uustal.heartrateserver.controller.api.response.HeartRateResponse;
import ee.uustal.heartrateserver.pojo.Event;
import ee.uustal.heartrateserver.service.sse.SseNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
@Service
public class HeartRateService {

    private static final String MEMBER_ID = "696969";

    private final MemCacheService memCacheService;
    private final SseNotificationService sseNotificationService;

    private final List<HeartRateRequest> requests = new ArrayList<>();
    private final List<HeartRateRequest> previousRequests = new ArrayList<>();

    public HeartRateResponse handle(HeartRateRequest request) {
        final File logfile = new File("hr.log");
        final String line = request.getTimestamp() + "; " + request.getHeartRate() + System.lineSeparator();

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(logfile, true));
            bw.append(line);
            bw.flush();
            bw.close();
        } catch (IOException e) {
            log.error("Error writing to file", e);
        }

        log.info("Received heart rate: {}", JsonUtility.toJson(request));
        requests.add(request);
        memCacheService.addHeartRate(request.getHeartRate());

        Map<String, Object> params = new HashMap<>();
        params.put("heartRate", request.getHeartRate());
        params.put("rssi", request.getRssi());
        params.put("timestamp", request.getTimestamp());
        sseNotificationService.sendNotification(MEMBER_ID, new Event("SSE_EVENT", params));

        return new HeartRateResponse()
                .setTimestamp(request.getTimestamp())
                .setHeartRate(request.getHeartRate())
                .setRssi(request.getRssi())
                .setHourlyAverage(memCacheService.getAverage(20))
                .setSixHourAverage(memCacheService.getAverage(360));
    }

    public HeartRateResponse getLatestHeartRate() {
        if (requests.isEmpty()) {
            return new HeartRateResponse();
        }
        final HeartRateRequest request = requests.get(requests.size() - 1);

        int duplicateRequestAmount = 0;
        if (!previousRequests.isEmpty()) {
            for (HeartRateRequest previousRequest : previousRequests) {
                if (request.equals(previousRequest)) {
                    duplicateRequestAmount++;
                }
            }

            if (previousRequests.size() == 10) {
                previousRequests.remove(0);
            }

            log.info(duplicateRequestAmount + " duplicate requests");
            if (duplicateRequestAmount == 10) {
                log.info("10 duplicate requests detected, sending empty response");
                previousRequests.add(request);
                return new HeartRateResponse();
            }
        }

        previousRequests.add(request);

        return new HeartRateResponse()
                .setTimestamp(request.getTimestamp())
                .setHeartRate(request.getHeartRate())
                .setRssi(request.getRssi())
                .setHourlyAverage(memCacheService.getAverage(20))
                .setSixHourAverage(memCacheService.getAverage(360));
    }
}
