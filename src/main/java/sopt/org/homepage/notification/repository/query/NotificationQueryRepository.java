package sopt.org.homepage.notification.repository.query;

import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.service.query.dto.NotificationListView;

/**
 * Notification Query Repository
 * - 읽기 작업만 담당 (조회)
 * - 복잡한 조회 쿼리는 QueryDSL로 구현
 */
public interface NotificationQueryRepository {

    /**
     * 특정 기수의 알림 목록 조회
     *
     * @param generation 조회할 기수
     * @return 해당 기수의 알림 신청 목록
     */
    NotificationListView findNotificationsByGeneration(Generation generation);
}