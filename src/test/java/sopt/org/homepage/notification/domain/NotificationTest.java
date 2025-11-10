package sopt.org.homepage.notification.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;

import static org.assertj.core.api.Assertions.*;

/**
 * Notification Domain Entity 단위 테스트
 * - Mock 없이 순수 도메인 로직 테스트
 */
@DisplayName("Notification Domain 테스트")
class NotificationTest {

    @Test
    @DisplayName("유효한 Email과 Generation으로 Notification 생성 성공")
    void create_WithValidEmailAndGeneration_Success() {
        // given
        Email email = new Email("test@sopt.org");
        Generation generation = new Generation(35);

        // when
        Notification notification = Notification.create(email, generation);

        // then
        assertThat(notification).isNotNull();
        assertThat(notification.getEmail()).isEqualTo(email);
        assertThat(notification.getGeneration()).isEqualTo(generation);
        assertThat(notification.getCreatedAt()).isNull(); // 아직 저장 전
    }

    @Test
    @DisplayName("Email이 null인 경우 예외 발생")
    void create_WithNullEmail_ThrowsException() {
        // given
        Email email = null;
        Generation generation = new Generation(35);

        // when & then
        assertThatThrownBy(() -> Notification.create(email, generation))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("이메일은 필수입니다");
    }

    @Test
    @DisplayName("Generation이 null인 경우 예외 발생")
    void create_WithNullGeneration_ThrowsException() {
        // given
        Email email = new Email("test@sopt.org");
        Generation generation = null;

        // when & then
        assertThatThrownBy(() -> Notification.create(email, generation))
                .isInstanceOf(BusinessLogicException.class)
                .hasMessageContaining("기수는 필수입니다");
    }

    @Test
    @DisplayName("같은 이메일과 기수 조합인지 확인")
    void isSameEmailAndGeneration_SameCombination_ReturnsTrue() {
        // given
        Email email = new Email("test@sopt.org");
        Generation generation = new Generation(35);
        Notification notification = Notification.create(email, generation);

        // when & then
        assertThat(notification.isSameEmailAndGeneration(email, generation)).isTrue();
    }

    @Test
    @DisplayName("다른 이메일과 기수 조합인 경우 false 반환")
    void isSameEmailAndGeneration_DifferentCombination_ReturnsFalse() {
        // given
        Email email1 = new Email("test1@sopt.org");
        Email email2 = new Email("test2@sopt.org");
        Generation generation1 = new Generation(35);
        Generation generation2 = new Generation(36);

        Notification notification = Notification.create(email1, generation1);

        // when & then
        assertThat(notification.isSameEmailAndGeneration(email2, generation1)).isFalse();
        assertThat(notification.isSameEmailAndGeneration(email1, generation2)).isFalse();
        assertThat(notification.isSameEmailAndGeneration(email2, generation2)).isFalse();
    }
}