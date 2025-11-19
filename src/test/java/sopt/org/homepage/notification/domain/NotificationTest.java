package sopt.org.homepage.notification.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;

/**
 * Notification Entity 단위 테스트
 * <p>
 * 목적: - 통합 테스트로 커버하기 어려운 엣지 케이스 검증 - 비즈니스 로직 (null 체크, 생성 규칙) 확인 - 빠른 피드백
 * <p>
 * 전략: - 행위 중심: "무엇을 하는가" - 구체 구현 최소화: ErrorCode, 정확한 메시지 X - 핵심만: 예외 발생 여부, 값 저장 여부
 */
@DisplayName("Notification Entity 단위 테스트")
class NotificationTest {

    // ===== 정상 케이스 =====

    @Test
    @DisplayName("유효한 Email과 Generation으로 생성하면 해당 값을 가진 Notification이 반환된다")
    void create_WithValidValues_ReturnsNotificationWithThoseValues() {
        // given
        Email email = new Email("test@sopt.org");
        Generation generation = new Generation(35);

        // when
        Notification notification = Notification.create(email, generation);

        // then - 행위: "값을 올바르게 저장한다"
        assertThat(notification.getEmail().getValue()).isEqualTo("test@sopt.org");
        assertThat(notification.getGeneration().getValue()).isEqualTo(35);
    }

    // ===== 예외 케이스 =====

    @Test
    @DisplayName("Email이 null이면 생성이 거부된다")
    void create_WithNullEmail_Rejected() {
        // given
        Email email = null;
        Generation generation = new Generation(35);

        // when & then - 행위: "생성을 거부한다"
        assertThatThrownBy(() -> Notification.create(email, generation))
                .isInstanceOf(RuntimeException.class)  // ✅ 예외 타입은 최소한만
                .hasMessageContaining("이메일");        // ✅ 키워드만 확인
    }

    @Test
    @DisplayName("Generation이 null이면 생성이 거부된다")
    void create_WithNullGeneration_Rejected() {
        // given
        Email email = new Email("test@sopt.org");
        Generation generation = null;

        // when & then - 행위: "생성을 거부한다"
        assertThatThrownBy(() -> Notification.create(email, generation))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("기수");
    }

    @Test
    @DisplayName("Email과 Generation이 모두 null이면 생성이 거부된다")
    void create_WithBothNull_Rejected() {
        // given
        Email email = null;
        Generation generation = null;

        // when & then - 행위: "생성을 거부한다"
        assertThatThrownBy(() -> Notification.create(email, generation))
                .isInstanceOf(RuntimeException.class);  // ✅ 메시지도 안 봄 (어차피 거부됨)
    }
}
