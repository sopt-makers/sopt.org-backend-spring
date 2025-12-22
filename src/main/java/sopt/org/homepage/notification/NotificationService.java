package sopt.org.homepage.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.notification.exception.DuplicateNotificationException;

/**
 * Notification Service
 * <p>
 * Command + Query 통합
 * <p>
 * 비즈니스 규칙: - 동일 이메일+기수 조합 중복 등록 불가
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    // ===== Command =====

    /**
     * 알림 등록
     *
     * @param email      이메일
     * @param generation 기수
     * @return 등록된 알림
     * @throws DuplicateNotificationException 중복 등록 시
     */
    @Transactional
    public Notification register(String email, Integer generation) {
        log.info("알림 등록 요청 - email={}, generation={}", email, generation);

        // 중복 검사
        if (notificationRepository.existsByEmailAndGeneration(email, generation)) {
            log.warn("중복 알림 등록 시도 차단 - email={}, generation={}", email, generation);
            throw new DuplicateNotificationException(email, generation);
        }

        // 저장
        Notification notification = Notification.of(email, generation);
        Notification saved = notificationRepository.save(notification);

        log.info("알림 등록 완료 - id={}", saved.getId());
        return saved;
    }

    // ===== Query =====

    /**
     * 특정 기수의 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public List<Notification> findByGeneration(Integer generation) {
        log.debug("알림 목록 조회 - generation={}", generation);
        return notificationRepository.findByGeneration(generation);
    }
}
