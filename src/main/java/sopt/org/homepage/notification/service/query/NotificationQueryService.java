package sopt.org.homepage.notification.service.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.repository.query.NotificationQueryRepository;
import sopt.org.homepage.notification.service.query.dto.NotificationListView;

/**
 * Notification Query Service
 * - 읽기 작업만 담당 (조회)
 * - Read-Only 트랜잭션으로 최적화
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;

    /**
     * 특정 기수의 알림 신청 목록 조회
     *
     * @param generationValue 조회할 기수 번호
     * @return 해당 기수의 이메일 목록
     */
    public NotificationListView getNotificationList(Integer generationValue) {
        // 1. Integer를 Generation VO로 변환 (검증 포함)
        Generation generation = new Generation(generationValue);

        // 2. 조회
        NotificationListView result = notificationQueryRepository
                .findNotificationsByGeneration(generation);

        log.info("Found {} notifications for generation {}",
                result.emailList().size(), generationValue);

        return result;
    }
}