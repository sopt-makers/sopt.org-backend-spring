package sopt.org.homepage.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import sopt.org.homepage.notification.domain.Notification;

@Schema(description = "알림 목록 조회 응답")
public record NotificationListResponse(

        @Schema(description = "기수", example = "35")
        Integer generation,

        @Schema(description = "이메일 목록")
        List<String> emailList
) {
    public static NotificationListResponse from(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            return new NotificationListResponse(null, List.of());
        }

        // 첫 번째 Notification의 generation 사용 (모두 같은 기수)
        Integer generation = notifications.get(0).getGeneration().getValue();

        // Email 목록 추출
        List<String> emailList = notifications.stream()
                .map(notification -> notification.getEmail().getValue())
                .toList();

        return new NotificationListResponse(generation, emailList);
    }


}
