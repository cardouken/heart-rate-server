package ee.uustal.heartrateserver.controller.api;

import ee.uustal.heartrateserver.controller.api.request.HeartRateRequest;
import ee.uustal.heartrateserver.controller.api.response.HeartRateResponse;
import ee.uustal.heartrateserver.service.HeartRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HeartRateController {

    private final HeartRateService heartRateService;

    @PostMapping(value = "/heart-rate")
    public HeartRateResponse postHeartRate(@RequestBody HeartRateRequest request) {
        return heartRateService.handle(request);
    }

    @GetMapping(value = "/heart-rate")
    public HeartRateResponse getHeartRate() {
        return heartRateService.getLatestHeartRate();
    }

}
