package sopt.org.homepage.notification.service.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.repository.command.NotificationCommandRepository;
import sopt.org.homepage.notification.service.IntegrationTestBase;
import sopt.org.homepage.notification.service.command.dto.NotificationResult;
import sopt.org.homepage.notification.service.command.dto.RegisterNotificationCommand;

import static org.assertj.core.api.Assertions.*;

/**
 * NotificationCommandService 통합 테스트
 * - 실제 DB 사용 (TestContainer)
 * - 트랜잭션 롤백으로 테스트 격리
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
    @DisplayName("유효한 Command로 알림 신청 등록 성공")
    void register_WithValidCommand_Success() {
        // given
        RegisterNotificationCommand command = new RegisterNotificationCommand(
                "test@sopt.org",
                35
        );

        // when
        NotificationResult result = commandService.register(command);

        // then
        assertThat(result.id()).isNotNull();
        assertThat(result.email()).isEqualTo("test@sopt.org");
        assertThat(result.generation()).isEqualTo(35);
        assertThat(result.createdAt()).isNotNull();

        // DB 검증
        Notification saved = commandRepository.findById(result.id()).orElseThrow();
        assertThat(saved.getEmail().getValue()).isEqualTo("test@sopt.org");
        assertThat(saved.getGeneration().getValue()).isEqualTo(35);
    }

    @Test
    @DisplayName("중복된 이메일과 기수 조합인 경우 예외 발생")
    void register_WithDuplicateEmailAndGeneration_ThrowsException() {
        // given
        RegisterNotificationCommand command = new RegisterNotificationCommand(
                "test@sopt.org",
                35
        );
        commandService.register(command); // 첫 번째 등록

        // when & then
        assertThatThrownBy(() -> commandService.register(command))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("이미 등록된 이메일입니다");
    }

    @Test
    @DisplayName("같은 이메일이지만 다른 기수인 경우 등록 성공")
    void register_SameEmailDifferentGeneration_Success() {
        // given
        RegisterNotificationCommand command1 = new RegisterNotificationCommand(
                "test@sopt.org",
                35
        );
        RegisterNotificationCommand command2 = new RegisterNotificationCommand(
                "test@sopt.org",
                36  // 다른 기수
        );

        // when
        NotificationResult result1 = commandService.register(command1);
        NotificationResult result2 = commandService.register(command2);

        // then
        assertThat(result1.id()).isNotNull();
        assertThat(result2.id()).isNotNull();
        assertThat(result1.id()).isNotEqualTo(result2.id());

        // DB 검증
        long count = commandRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("유효하지 않은 이메일 형식인 경우 예외 발생")
    void register_WithInvalidEmailFormat_ThrowsException() {
        // given
        RegisterNotificationCommand command = new RegisterNotificationCommand(
                "invalid-email",  // 잘못된 형식
                35
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(command))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("유효하지 않은 이메일 형식입니다");
    }

    @Test
    @DisplayName("유효하지 않은 기수인 경우 예외 발생")
    void register_WithInvalidGeneration_ThrowsException() {
        // given
        RegisterNotificationCommand command = new RegisterNotificationCommand(
                "test@sopt.org",
                999  // 범위 초과
        );

        // when & then
        assertThatThrownBy(() -> commandService.register(command))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("기수는 100기 이하여야 합니다");
    }
}