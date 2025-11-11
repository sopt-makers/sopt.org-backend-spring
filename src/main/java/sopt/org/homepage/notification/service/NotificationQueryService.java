package sopt.org.homepage.notification.service.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.repository.query.NotificationQueryRepository;


/**
 * Notification Query Service - 읽기 작업만 담당 (조회) - Read-Only 트랜잭션으로 최적화
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationQueryService {

    private final NotificationQueryRepository notificationQueryRepository;

    public List<Notification> getNotificationList(Integer generationValue) {
        // 1. Integer를 Generation VO로 변환 (검증 포함)
        Generation generation = new Generation(generationValue);

        // 2. 조회
        List<Notification> notifications =
                notificationQueryRepository.findByGeneration(generation);

        return notifications;
    }
}
