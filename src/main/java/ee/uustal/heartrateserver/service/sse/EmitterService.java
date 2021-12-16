package ee.uustal.heartrateserver.service.sse;

import ee.uustal.heartrateserver.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Log4j2
@RequiredArgsConstructor
public class EmitterService {

    private static final String MEMBER_ID = "69";
    private static final long EVENTS_TIMEOUT = 150_000;
    private final EmitterRepository repository;

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(EVENTS_TIMEOUT);
        emitter.onCompletion(() -> {
            log.info("Emitter for member {} closed", MEMBER_ID);
            repository.remove(MEMBER_ID);
        });
        emitter.onTimeout(() -> {
            log.info("Emitter timeout");
            repository.remove(MEMBER_ID);
        });
        emitter.onError(e -> {
            log.error("Create SseEmitter exception", e);
            repository.remove(MEMBER_ID);
        });
        repository.addOrReplaceEmitter(MEMBER_ID, emitter);
        return emitter;
    }

}