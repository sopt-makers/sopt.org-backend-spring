package sopt.org.homepage.notification.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.repository.NotificationCommandRepository;
import sopt.org.homepage.notification.service.NotificationCommandService;

/**
 * NotificationCommandService 통합 테스트 (고전파 스타일) 테스트 전략: - 실제 DB 사용 (TestContainer) - Mock 없이 전체 흐름 검증 - 행위 중심: "무엇을 하는가"에
 * 집중 - 구체 구현 최소화: ErrorCode 검증 제거 목적: - 실제 시스템이 제대로 동작하는지 핵심 비즈니스 사용자 시나리오 검증 - 여러 컴포넌트의 협력 확인 - 실제 DB 사용한 전체 흐름 검증 *
 * 제외: - 입력값 검증 (VO 단위 테스트에서 담당) - 경계값 테스트 (VO 단위 테스트에서 담당)
 *
 *
 */
@DisplayName("알림 등록 통합 테스트")
class NotificationCommandServiceTest extends IntegrationTestBase {

    @Autowired
    private NotificationCommandService commandService;

    @Autowired
    private NotificationCommandRepository commandRepository;

    @AfterEach
    void tearDown() {
        commandRepository.deleteAll();
    }

    // ===== 정상 등록 시나리오 =====

    @Test
    @DisplayName("유효한 요청으로 알림을 등록하면 DB에 저장된다")
    void register_WithValidRequest_SavedInDB() {
        // given
        var request = new RegisterNotificationRequest("test@sopt.org", 35);

        // when - 행위: "알림을 등록한다"
        Notification result = commandService.register(request);

        // then - 행위 결과: "DB에 저장되어 있다"
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail().getValue()).isEqualTo("test@sopt.org");
        assertThat(result.getGeneration().getValue()).isEqualTo(35);
        assertThat(result.getCreatedAt()).isNotNull();

        // DB 확인
        Notification saved = commandRepository.findById(result.getId()).orElseThrow();
        assertThat(saved.getEmail().getValue()).isEqualTo("test@sopt.org");
        assertThat(saved.getGeneration().getValue()).isEqualTo(35);
    }

    @Test
    @DisplayName("여러 알림을 등록하면 모두 DB에 저장된다")
    void register_MultipleNotifications_AllSaved() {
        // when
        commandService.register(new RegisterNotificationRequest("user1@sopt.org", 35));
        commandService.register(new RegisterNotificationRequest("user2@sopt.org", 35));
        commandService.register(new RegisterNotificationRequest("user3@sopt.org", 36));

        // then
        assertThat(commandRepository.count()).isEqualTo(3);
    }

    // ===== 중복 등록 시나리오 =====

    @Test
    @DisplayName("같은 이메일과 기수로 두 번 등록하면 두 번째는 거부된다")
    void register_Duplicate_SecondRejected() {
        // given
        var request = new RegisterNotificationRequest("test@sopt.org", 35);
        commandService.register(request);

        // when & then - 행위: "중복 등록이 거부된다"
        assertThatThrownBy(() -> commandService.register(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("이미 등록된 알림")
                .hasMessageContaining("test@sopt.org");

        // 행위 결과: "DB에는 1건만 있다"
        assertThat(commandRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("같은 이메일이지만 다른 기수면 둘 다 등록된다")
    void register_SameEmailDifferentGeneration_BothSaved() {
        // given
        var request1 = new RegisterNotificationRequest("test@sopt.org", 35);
        var request2 = new RegisterNotificationRequest("test@sopt.org", 36);

        // when - 행위: "같은 이메일, 다른 기수로 등록한다"
        Notification result1 = commandService.register(request1);
        Notification result2 = commandService.register(request2);

        // then - 행위 결과: "둘 다 저장된다"
        assertThat(result1.getId()).isNotEqualTo(result2.getId());
        assertThat(commandRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("다른 이메일이지만 같은 기수면 둘 다 등록된다")
    void register_DifferentEmailSameGeneration_BothSaved() {
        // given
        var request1 = new RegisterNotificationRequest("test1@sopt.org", 35);
        var request2 = new RegisterNotificationRequest("test2@sopt.org", 35);

        // when - 행위: "다른 이메일, 같은 기수로 등록한다"
        Notification result1 = commandService.register(request1);
        Notification result2 = commandService.register(request2);

        // then - 행위 결과: "둘 다 저장된다"
        assertThat(result1.getId()).isNotEqualTo(result2.getId());
        assertThat(commandRepository.count()).isEqualTo(2);
    }

}
