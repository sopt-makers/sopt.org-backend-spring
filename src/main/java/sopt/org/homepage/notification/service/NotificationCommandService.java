package sopt.org.homepage.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.exception.NotificationDomainException;
import sopt.org.homepage.notification.repository.NotificationCommandRepository;

/**
 * Notification Command Service - 쓰기 작업만 담당 (등록) - 얇은 Service 계층: 도메인에 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService {

    private final NotificationCommandRepository notificationCommandRepository;

    public Notification register(RegisterNotificationRequest request) {

        // 1. Command를 VO로 변환 (검증 포함)
        Email email = new Email(request.email());
        Generation generation = new Generation(request.generation());
        // 2. 중복 검사
        validateNotDuplicate(email, generation);

        // 3. 도메인 생성 (팩토리 메서드 사용)
        Notification notification = Notification.create(email, generation);

        // 4. 저장 후 결과 반환
        return notificationCommandRepository.save(notification);
    }

    private void validateNotDuplicate(Email email, Generation generation) {
        if (notificationCommandRepository.existsByEmailAndGeneration(email, generation)) {
            log.warn("[Business] 중복 알림 시도 차단 - email={}, generation={}",
                    email.getValue(), generation.getValue());
            throw NotificationDomainException.duplicateNotification(email.getValue(), generation.getValue());
        }
    }
}
