package ee.uustal.heartrateserver.controller.api;

import ee.uustal.heartrateserver.service.sse.EmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Log4j2
@RestController
@RequiredArgsConstructor
public class EventController {

    private final EmitterService emitterService;

    @GetMapping(value = "/events")
    public SseEmitter subscribe() {
        log.info("Subscribing new member");
        return emitterService.createEmitter();
    }

}
