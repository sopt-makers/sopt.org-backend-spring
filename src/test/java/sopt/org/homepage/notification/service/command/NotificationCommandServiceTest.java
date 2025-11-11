package sopt.org.homepage.notification.service.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.notification.controller.dto.RegisterNotificationRequest;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.exception.DuplicateNotificationException;
import sopt.org.homepage.notification.repository.command.NotificationCommandRepository;

import static org.assertj.core.api.Assertions.*;

/**
 * NotificationCommandService 통합 테스트
 * - 실제 DB 사용 (TestContainer)
 * - 트랜잭션 롤백으로 테스트 격리
 * - Request DTO를 직접 사용하여 Service 테스트
 */
@DisplayName("NotificationCommandService 통합 테스트")
class NotificationCommandServiceTest extends IntegrationTestBase {

    @Autowired
    private NotificationCommandService commandService;

    @Autowired
    private NotificationCommandRepository commandRepository;

    @AfterEach
    void tearDown() {
        commandRepository.deleteAll();
    }

    @Test
    @DisplayName("유효한 Request로 알림 신청 등록 성공")
    void register_WithValidRequest_Success() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                35
        );

        // when
        Notification result = commandService.register(request);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getEmail().getValue()).isEqualTo("test@sopt.org");
        assertThat(result.getGeneration().getValue()).isEqualTo(35);
        assertThat(result.getCreatedAt()).isNotNull();

        // DB 검증
        Notification saved = commandRepository.findById(result.getId()).orElseThrow();
        assertThat(saved.getEmail().getValue()).isEqualTo("test@sopt.org");
        assertThat(saved.getGeneration().getValue()).isEqualTo(35);
    }

    @Test
    @DisplayName("중복된 이메일과 기수 조합인 경우 예외 발생")
    void register_WithDuplicateEmailAndGeneration_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                35
        );
        commandService.register(request); // 첫 번째 등록

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .isInstanceOf(DuplicateNotificationException.class)
                .hasMessageContaining("이미 등록된 알림입니다");
    }

    @Test
    @DisplayName("같은 이메일이지만 다른 기수인 경우 등록 성공")
    void register_SameEmailDifferentGeneration_Success() {
        // given
        RegisterNotificationRequest request1 = new RegisterNotificationRequest(
                "test@sopt.org",
                35
        );
        RegisterNotificationRequest request2 = new RegisterNotificationRequest(
                "test@sopt.org",
                36  // 다른 기수
        );

        // when
        Notification result1 = commandService.register(request1);
        Notification result2 = commandService.register(request2);

        // then
        assertThat(result1.getId()).isNotNull();
        assertThat(result2.getId()).isNotNull();
        assertThat(result1.getId()).isNotEqualTo(result2.getId());

        // DB 검증
        long count = commandRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("다른 이메일이지만 같은 기수인 경우 등록 성공")
    void register_DifferentEmailSameGeneration_Success() {
        // given
        RegisterNotificationRequest request1 = new RegisterNotificationRequest(
                "test1@sopt.org",
                35
        );
        RegisterNotificationRequest request2 = new RegisterNotificationRequest(
                "test2@sopt.org",
                35  // 같은 기수
        );

        // when
        Notification result1 = commandService.register(request1);
        Notification result2 = commandService.register(request2);

        // then
        assertThat(result1.getId()).isNotNull();
        assertThat(result2.getId()).isNotNull();
        assertThat(result1.getId()).isNotEqualTo(result2.getId());

        // DB 검증
        long count = commandRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("유효하지 않은 이메일 형식인 경우 예외 발생 - @ 누락")
    void register_WithInvalidEmailFormat_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "invalid-email",  // @ 없는 잘못된 형식
                35
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .hasMessageContaining("유효하지 않은 이메일 형식입니다");
    }

    @Test
    @DisplayName("이메일이 null인 경우 예외 발생")
    void register_WithNullEmail_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                null,  // null 이메일
                35
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("이메일이 빈 문자열인 경우 예외 발생")
    void register_WithEmptyEmail_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "",  // 빈 문자열
                35
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .hasMessageContaining("이메일");
    }

    @Test
    @DisplayName("기수가 0인 경우 예외 발생")
    void register_WithZeroGeneration_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                0  // 0은 유효하지 않음
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .hasMessageContaining("기수");
    }

    @Test
    @DisplayName("기수가 음수인 경우 예외 발생")
    void register_WithNegativeGeneration_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                -1  // 음수는 유효하지 않음
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .hasMessageContaining("기수");
    }

    @Test
    @DisplayName("기수가 100을 초과하는 경우 예외 발생")
    void register_WithGenerationOver100_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                101  // 100 초과
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .hasMessageContaining("기수는 100기 이하여야 합니다");
    }

    @Test
    @DisplayName("기수가 null인 경우 예외 발생")
    void register_WithNullGeneration_ThrowsException() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                null  // null 기수
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(request))
                .hasMessageContaining("기수");
    }

    @Test
    @DisplayName("경계값 테스트 - 기수 1 (최소값)")
    void register_WithMinimumGeneration_Success() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                1  // 최소 유효값
        );

        // when
        Notification result = commandService.register(request);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getGeneration().getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("경계값 테스트 - 기수 100 (최대값)")
    void register_WithMaximumGeneration_Success() {
        // given
        RegisterNotificationRequest request = new RegisterNotificationRequest(
                "test@sopt.org",
                100  // 최대 유효값
        );

        // when
        Notification result = commandService.register(request);

        // then
        assertThat(result.getId()).isNotNull();
        assertThat(result.getGeneration().getValue()).isEqualTo(100);
    }

    @Test
    @DisplayName("여러 개의 알림을 순차적으로 등록")
    void register_MultipleNotifications_Success() {
        // given & when
        Notification result1 = commandService.register(
                new RegisterNotificationRequest("user1@sopt.org", 35)
        );
        Notification result2 = commandService.register(
                new RegisterNotificationRequest("user2@sopt.org", 35)
        );
        Notification result3 = commandService.register(
                new RegisterNotificationRequest("user3@sopt.org", 36)
        );

        // then
        assertThat(commandRepository.count()).isEqualTo(3);
        assertThat(result1.getId()).isNotEqualTo(result2.getId());
        assertThat(result2.getId()).isNotEqualTo(result3.getId());
    }
}
