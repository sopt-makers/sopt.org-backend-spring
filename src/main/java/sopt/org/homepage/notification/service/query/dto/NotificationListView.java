package sopt.org.homepage.notification.service.query.dto;

import java.util.List;

/**
 * Notification 목록 조회 결과
 */
public record NotificationListView(
        Integer generation,
        List<String> emailList
) {
    /**
     * 정적 팩토리 메서드
     */
    public static NotificationListView of(Integer generation, List<String> emailList) {
        return new NotificationListView(generation, emailList);
    }

    /**
     * Compact Constructor: 방어적 복사
     */
    public NotificationListView {
        emailList = List.copyOf(emailList); // 불변 리스트로 변환
    }
}