package sopt.org.homepage.notification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.notification.exception.DuplicateNotificationException;

/**
 * Notification 통합 테스트
 * <p>
 * 인수인계 목적: - 테스트를 읽으면 비즈니스 규칙을 이해할 수 있음 - 시나리오 기반 테스트
 */
@DisplayName("알림 서비스 통합 테스트")
class NotificationServiceTest extends IntegrationTestBase {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;


    @AfterEach
    void tearDown() {
        notificationRepository.deleteAll();  // ✅ 매 테스트 후 정리
    }

    // ===== 등록 시나리오 =====

    @Nested
    @DisplayName("알림 등록")
    class Register {

        @Test
        @DisplayName("✅ 정상: 유효한 이메일과 기수로 알림 등록")
        void register_WithValidInput_Success() {
            // given
            String email = "test@sopt.org";
            Integer generation = 35;

            // when
            Notification result = notificationService.register(email, generation);

            // then
            assertThat(result.getId()).isNotNull();
            assertThat(result.getEmail()).isEqualTo(email);
            assertThat(result.getGeneration()).isEqualTo(generation);
            assertThat(result.getCreatedAt()).isNotNull();
        }

        @Test
        @DisplayName("❌ 실패: 동일 이메일+기수 중복 등록 불가")
        void register_WithDuplicate_ThrowsException() {
            // given
            String email = "duplicate@sopt.org";
            Integer generation = 35;
            notificationService.register(email, generation);

            // when & then
            assertThatThrownBy(() -> notificationService.register(email, generation))
                    .isInstanceOf(DuplicateNotificationException.class)
                    .hasMessageContaining("이미 등록된 알림")
                    .hasMessageContaining(email);
        }

        @Test
        @DisplayName("✅ 정상: 같은 이메일이지만 다른 기수는 등록 가능")
        void register_SameEmailDifferentGeneration_Success() {
            // given
            String email = "test@sopt.org";
            notificationService.register(email, 35);

            // when
            Notification result = notificationService.register(email, 36);

            // then
            assertThat(result.getGeneration()).isEqualTo(36);
            assertThat(notificationRepository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ 정상: 다른 이메일이지만 같은 기수는 등록 가능")
        void register_DifferentEmailSameGeneration_Success() {
            // given
            Integer generation = 35;
            notificationService.register("user1@sopt.org", generation);

            // when
            Notification result = notificationService.register("user2@sopt.org", generation);

            // then
            assertThat(result.getEmail()).isEqualTo("user2@sopt.org");
            assertThat(notificationRepository.count()).isEqualTo(2);
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("알림 조회")
    class FindByGeneration {

        @Test
        @DisplayName("✅ 조회: 특정 기수의 알림 목록")
        void findByGeneration_ReturnsMatchingNotifications() {
            // given - 35기 2건, 36기 1건
            notificationService.register("user1@sopt.org", 35);
            notificationService.register("user2@sopt.org", 35);
            notificationService.register("user3@sopt.org", 36);

            // when
            List<Notification> result = notificationService.findByGeneration(35);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(n -> n.getGeneration().equals(35));
        }

        @Test
        @DisplayName("✅ 조회: 등록된 알림이 없으면 빈 목록")
        void findByGeneration_WhenEmpty_ReturnsEmptyList() {
            // given - 아무것도 등록 안 함

            // when
            List<Notification> result = notificationService.findByGeneration(99);

            // then
            assertThat(result).isEmpty();
        }
    }
}
