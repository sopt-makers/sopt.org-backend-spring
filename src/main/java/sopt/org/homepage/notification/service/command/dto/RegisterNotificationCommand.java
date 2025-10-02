package sopt.org.homepage.notification.service.command.dto;

import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;

/**
 * Notification 등록 Command
 * - Controller에서 @Valid로 기본 검증 완료
 * - Domain VO에서 상세 검증 수행
 * - Command 자체는 검증 로직 불필요
 */
public record RegisterNotificationCommand(
        String email,
        Integer generation
) {
    /**
     * Command를 Domain VO로 변환
     * VO 생성 시점에 검증 수행됨
     */
    public Email toEmailVo() {
        return new Email(email);  // ← 여기서 검증!
    }

    public Generation toGenerationVo() {
        return new Generation(generation);  // ← 여기서 검증!
    }

    /**
     * Request DTO에서 Command 생성
     */
    public static RegisterNotificationCommand from(
            sopt.org.homepage.notification.controller.dto.RegisterNotificationRequest request
    ) {
        return new RegisterNotificationCommand(
                request.email(),
                request.generation()
        );
    }
}