package sopt.org.homepage.notification.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.repository.NotificationCommandRepository;
import sopt.org.homepage.notification.service.NotificationQueryService;

/**
 * NotificationQueryService 통합 테스트
 */
@DisplayName("NotificationQueryService 통합 테스트")
class NotificationQueryServiceTest extends IntegrationTestBase {

    @Autowired
    private NotificationQueryService queryService;

    @Autowired
    private NotificationCommandRepository commandRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        Notification notification1 = Notification.create(
                new Email("test1@sopt.org"),
                new Generation(35)
        );
        Notification notification2 = Notification.create(
                new Email("test2@sopt.org"),
                new Generation(35)
        );
        Notification notification3 = Notification.create(
                new Email("test3@sopt.org"),
                new Generation(36)  // 다른 기수
        );

        commandRepository.saveAll(List.of(notification1, notification2, notification3));
    }

    @AfterEach
    void tearDown() {
        commandRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 기수의 알림 목록 조회 성공")
    void getNotificationList_WithValidGeneration_Success() {
        // when
        List<Notification> notifications = queryService.getNotificationList(35);

        // then
        assertThat(notifications).hasSize(2);
        assertThat(notifications)
                .extracting(n -> n.getEmail().getValue())
                .containsExactlyInAnyOrder("test1@sopt.org", "test2@sopt.org");

        // 모두 같은 기수인지 확인
        assertThat(notifications)
                .allMatch(n -> n.getGeneration().getValue().equals(35));
    }

    @Test
    @DisplayName("알림이 없는 기수 조회 시 빈 리스트 반환")
    void getNotificationList_NoNotifications_ReturnsEmptyList() {
        // when
        List<Notification> notifications = queryService.getNotificationList(99);

        // then
        assertThat(notifications).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 기수로 조회 시 예외 발생")
    void getNotificationList_WithInvalidGeneration_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> queryService.getNotificationList(999))
                .hasMessageContaining("기수는 100기 이하여야 합니다");
    }

    @Test
    @DisplayName("기수가 0인 경우 예외 발생")
    void getNotificationList_WithZeroGeneration_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> queryService.getNotificationList(0))
                .hasMessageContaining("기수");
    }

    @Test
    @DisplayName("기수가 음수인 경우 예외 발생")
    void getNotificationList_WithNegativeGeneration_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> queryService.getNotificationList(-1))
                .hasMessageContaining("기수");
    }
}

