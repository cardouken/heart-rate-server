package ee.uustal.heartrateserver.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
@Log4j2
public class InMemoryEmitterRepository implements EmitterRepository {

    private Map<String, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();

    @Override
    public void addOrReplaceEmitter(String memberId, SseEmitter emitter) {
        userEmitterMap.put(memberId, emitter);
    }

    @Override
    public void remove(String memberId) {
        if (userEmitterMap != null && userEmitterMap.containsKey(memberId)) {
            log.info("Removing emitter for member: {}", memberId);
            userEmitterMap.remove(memberId);
        } else {
            log.info("No emitter to remove for member: {}", memberId);
        }
    }

    @Override
    public Optional<SseEmitter> get(String memberId) {
        return Optional.ofNullable(userEmitterMap.get(memberId));
    }
}