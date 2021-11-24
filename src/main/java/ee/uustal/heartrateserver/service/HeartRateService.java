package ee.uustal.heartrateserver.service;

import ee.uustal.heartrateserver.config.JsonUtility;
import ee.uustal.heartrateserver.controller.api.request.HeartRateRequest;
import ee.uustal.heartrateserver.controller.api.response.HeartRateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@RequiredArgsConstructor
@Service
public class HeartRateService {

    private final List<HeartRateRequest> requests = new ArrayList<>();

    public HeartRateResponse handle(HeartRateRequest request) {
        final HeartRateResponse response = new HeartRateResponse()
                .setTimestamp(request.getTimestamp())
                .setHeartRate(request.getHeartRate());

        requests.add(request);
        log.info("Received heart rate: {}", JsonUtility.toJson(response));
        return response;
    }

    public HeartRateResponse getLatestHeartRate() {
        final HeartRateRequest request = requests.get(requests.size() - 1);
        return new HeartRateResponse()
                .setTimestamp(request.getTimestamp())
                .setHeartRate(request.getHeartRate());

    }
}
