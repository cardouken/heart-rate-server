package ee.uustal.heartrateserver.controller.api.response;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
public class HeartRateResponse {

    private int heartRate;
    private LocalDateTime timestamp;
    private int rssi;

}
