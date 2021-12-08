package ee.uustal.heartrateserver.service.sse;

import ee.uustal.heartrateserver.pojo.Event;

public interface NotificationService {

    void sendNotification(String memberId, Event event);

}