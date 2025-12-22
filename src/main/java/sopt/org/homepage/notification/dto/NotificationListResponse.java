package sopt.org.homepage.notification.dto;

import java.util.List;
import sopt.org.homepage.notification.Notification;

public record NotificationListResponse(
        Integer generation,
        List<String> emailList
) {
    public static NotificationListResponse from(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            return new NotificationListResponse(null, List.of());
        }

        Integer generation = notifications.get(0).getGeneration();  // ✅ 직접 접근
        List<String> emailList = notifications.stream()
                .map(Notification::getEmail)                          // ✅ 직접 접근
                .toList();

        return new NotificationListResponse(generation, emailList);
    }
}
