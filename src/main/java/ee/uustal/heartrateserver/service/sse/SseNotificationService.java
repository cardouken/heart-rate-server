package ee.uustal.heartrateserver.service.sse;

import ee.uustal.heartrateserver.pojo.Event;
import ee.uustal.heartrateserver.repository.EmitterRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Primary
@AllArgsConstructor
@Log4j2
public class SseNotificationService implements NotificationService {

    private final EmitterRepository emitterRepository;
    private final EventMapper eventMapper;

    @Override
    public void sendNotification(String memberId, Event event) {
        if (event == null) {
            log.info("No server event to send to device.");
            return;
        }
        doSendNotification(memberId, event);
    }

    private void doSendNotification(String memberId, Event event) {
        emitterRepository.get(memberId).ifPresentOrElse(sseEmitter -> {
            try {
                log.info("Sending event: {} for member: {}", event, memberId);
                sseEmitter.send(eventMapper.toSseEventBuilder(event));
            } catch (IOException | IllegalStateException e) {
                log.info("Error while sending event: {} for member: {} - exception: {}", event, memberId, e);
                emitterRepository.remove(memberId);
            }
        }, () -> log.info("No emitter for member {}", memberId));
    }
}