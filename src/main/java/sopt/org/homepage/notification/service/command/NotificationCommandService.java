package sopt.org.homepage.notification.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.exception.DuplicateNotificationException;
import sopt.org.homepage.notification.repository.command.NotificationCommandRepository;
import sopt.org.homepage.notification.service.command.dto.NotificationResult;
import sopt.org.homepage.notification.service.command.dto.RegisterNotificationCommand;

/**
 * Notification Command Service
 * - 쓰기 작업만 담당 (등록)
 * - 얇은 Service 계층: 도메인에 위임
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificationCommandService {

    private final NotificationCommandRepository notificationCommandRepository;

    /**
     * 모집 알림 신청 등록
     *
     * @param command 등록 명령
     * @return 등록된 알림 정보
     * @throws ClientBadRequestException 이미 등록된 이메일인 경우
     */
    public NotificationResult register(RegisterNotificationCommand command) {

        log.info("모집 알림 신청 시작 - email: {}, generation: {}",
                command.email(), command.generation());


        // 1. Command를 VO로 변환 (검증 포함)
        Email email = command.toEmailVo();
        Generation generation = command.toGenerationVo();

        // 2. 중복 검사
        validateNotDuplicate(email, generation);

        // 3. 도메인 생성 (팩토리 메서드 사용)
        Notification notification = Notification.create(email, generation);

        // 4. 저장
        Notification saved = notificationCommandRepository.save(notification);

        log.info("모집 알림 신청 완료 - id: {}, email: {}, generation: {}",
                saved.getId(), email.getValue(), generation.getValue());



        // 5. 결과 반환
        return NotificationResult.from(saved);
    }

    /**
     * 중복 검사
     * - exists 쿼리로 최적화
     */
    private void validateNotDuplicate(Email email, Generation generation) {
        if (notificationCommandRepository.existsByEmailAndGeneration(email, generation)) {
            throw new DuplicateNotificationException(
                    email.getValue(),
                    generation.getValue()
            );
        }
    }
}