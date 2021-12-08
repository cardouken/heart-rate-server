package ee.uustal.heartrateserver.service.sse;

import ee.uustal.heartrateserver.repository.EmitterRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@Log4j2
public class EmitterService {

    private static final String MEMBER_ID = "696969";
    private final long eventsTimeout;
    private final EmitterRepository repository;

    public EmitterService(@Value("180000") long eventsTimeout,
                          EmitterRepository repository) {
        this.eventsTimeout = eventsTimeout;
        this.repository = repository;
    }

    public SseEmitter createEmitter() {
        SseEmitter emitter = new SseEmitter(eventsTimeout);
        emitter.onCompletion(() -> repository.remove(MEMBER_ID));
        emitter.onTimeout(() -> repository.remove(MEMBER_ID));
        emitter.onError(e -> {
            log.error("Create SseEmitter exception", e);
            repository.remove(MEMBER_ID);
        });
        repository.addOrReplaceEmitter(MEMBER_ID, emitter);
        return emitter;
    }

}