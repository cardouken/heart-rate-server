package ee.uustal.heartrateserver.service.sse;

import ee.uustal.heartrateserver.pojo.Event;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class EventMapper {

    public SseEmitter.SseEventBuilder toSseEventBuilder(Event event) {
        return SseEmitter.event()
                .id(UUID.randomUUID().toString())
                .name(event.getType())
                .data(event.getBody());
    }
}