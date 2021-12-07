package ee.uustal.heartrateserver.service;

import ee.uustal.heartrateserver.config.JsonUtility;
import ee.uustal.heartrateserver.controller.api.request.HeartRateRequest;
import ee.uustal.heartrateserver.controller.api.response.HeartRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class HeartRateService {

    private final MemCacheService memCacheService;

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

        return new HeartRateResponse()
                .setTimestamp(request.getTimestamp())
                .setHeartRate(request.getHeartRate())
                .setRssi(request.getRssi());
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
