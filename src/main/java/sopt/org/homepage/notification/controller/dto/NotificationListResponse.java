package sopt.org.homepage.notification.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.notification.service.query.dto.NotificationListView;

import java.util.List;

/**
 * 알림 목록 조회 응답
 */
@Schema(description = "알림 목록 조회 응답")
public record NotificationListResponse(

        @Schema(description = "기수", example = "35")
        Integer generation,

        @Schema(description = "이메일 목록")
        List<String> emailList
) {
    /**
     * Service View를 Response로 변환
     */
    public static NotificationListResponse from(NotificationListView view) {
        return new NotificationListResponse(
                view.generation(),
                view.emailList()
        );
    }

    /**
     * Compact Constructor: 불변 리스트 보장
     */
    public NotificationListResponse {
        emailList = List.copyOf(emailList);
    }
}