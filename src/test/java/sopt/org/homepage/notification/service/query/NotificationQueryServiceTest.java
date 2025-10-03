package sopt.org.homepage.notification.service.query;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.repository.command.NotificationCommandRepository;
import sopt.org.homepage.notification.service.IntegrationTestBase;
import sopt.org.homepage.notification.service.query.dto.NotificationListView;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

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
        NotificationListView view = queryService.getNotificationList(35);

        // then
        assertThat(view.generation()).isEqualTo(35);
        assertThat(view.emailList()).hasSize(2);
        assertThat(view.emailList()).containsExactlyInAnyOrder(
                "test1@sopt.org",
                "test2@sopt.org"
        );
    }

    @Test
    @DisplayName("알림이 없는 기수 조회 시 빈 리스트 반환")
    void getNotificationList_NoNotifications_ReturnsEmptyList() {
        // when
        NotificationListView view = queryService.getNotificationList(99);

        // then
        assertThat(view.generation()).isEqualTo(99);
        assertThat(view.emailList()).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 기수로 조회 시 예외 발생")
    void getNotificationList_WithInvalidGeneration_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> queryService.getNotificationList(999))
                .hasMessageContaining("기수는 100기 이하여야 합니다");
    }
}