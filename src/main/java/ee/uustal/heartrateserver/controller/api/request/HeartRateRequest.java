package ee.uustal.heartrateserver.controller.api.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Getter
@Setter
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class HeartRateRequest {

    private int heartRate;
    private int rssi;
    private LocalDateTime timestamp;

}
