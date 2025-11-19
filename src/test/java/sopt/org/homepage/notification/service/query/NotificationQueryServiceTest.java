package sopt.org.homepage.notification.service.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
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
 * NotificationQueryService 통합 테스트 (고전파 스타일)
 * <p>
 * 테스트 전략: - 실제 DB 사용 (TestContainer) - Mock 없이 전체 흐름 검증 - 행위 중심: 조회 결과 검증
 * <p>
 * 목적: - 핵심 조회 시나리오 검증 - 빈 결과 처리 확인 - DB 쿼리 정상 동작 확인
 * <p>
 * 제외: - 입력값 검증 (VO 단위 테스트에서 담당)
 */
@DisplayName("알림 조회 통합 테스트")
class NotificationQueryServiceTest extends IntegrationTestBase {

    @Autowired
    private NotificationQueryService queryService;

    @Autowired
    private NotificationCommandRepository commandRepository;

    @AfterEach
    void tearDown() {
        commandRepository.deleteAll();
    }

    // ===== 핵심 조회 시나리오 =====

    @Test
    @DisplayName("특정 기수의 알림을 조회하면 해당 기수의 알림들이 반환된다")
    void getNotificationList_WithGeneration_ReturnsNotificationsOfThatGeneration() {
        // given - 35기 2건, 36기 1건 등록
        commandRepository.save(Notification.create(
                new Email("test1@sopt.org"),
                new Generation(35)
        ));
        commandRepository.save(Notification.create(
                new Email("test2@sopt.org"),
                new Generation(35)
        ));
        commandRepository.save(Notification.create(
                new Email("test3@sopt.org"),
                new Generation(36)
        ));

        // when - 행위: "35기 알림을 조회한다"
        List<Notification> notifications = queryService.getNotificationList(35);

        // then - 행위 결과: "35기 알림 2건이 반환된다"
        assertThat(notifications).hasSize(2);
        assertThat(notifications)
                .extracting(n -> n.getEmail().getValue())
                .containsExactlyInAnyOrder("test1@sopt.org", "test2@sopt.org");
        assertThat(notifications)
                .allMatch(n -> n.getGeneration().getValue().equals(35));
    }

    @Test
    @DisplayName("알림이 없는 기수를 조회하면 빈 리스트가 반환된다")
    void getNotificationList_WithNoNotifications_ReturnsEmptyList() {
        // given - 35기만 등록
        commandRepository.save(Notification.create(
                new Email("test@sopt.org"),
                new Generation(35)
        ));

        // when - 행위: "알림이 없는 99기를 조회한다"
        List<Notification> notifications = queryService.getNotificationList(99);

        // then - 행위 결과: "빈 리스트가 반환된다"
        assertThat(notifications).isEmpty();
    }

    @Test
    @DisplayName("아무 데이터도 없을 때 조회하면 빈 리스트가 반환된다")
    void getNotificationList_WithNoData_ReturnsEmptyList() {
        // when - 행위: "데이터 없이 조회한다"
        List<Notification> notifications = queryService.getNotificationList(35);

        // then - 행위 결과: "빈 리스트가 반환된다"
        assertThat(notifications).isEmpty();
    }
}
