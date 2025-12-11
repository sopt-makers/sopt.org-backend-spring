package sopt.org.homepage.notification.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sopt.org.homepage.notification.exception.NotificationDomainException;

/**
 * Email VO 단위 테스트 - Mock 없이 순수 Java 객체 테스트 - 검증 로직 집중 테스트
 */
@DisplayName("Email VO 테스트")
class EmailTest {


    // 단일 케이스에 대해, 예외 여부뿐 아니라 내부 상태까지 검증.
    // VO는 값 그 자체가 정체성이며, 내부 상태 = 공개 API 이니  구현이 아니라행위를 테스트하는것이다.
    @Test
    @DisplayName("유효한 이메일로 Email VO 생성 성공")
    void createEmail_WithValidEmail_Success() {
        // given
        String validEmail = "test@sopt.org";

        // when
        Email email = new Email(validEmail);

        // then
        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @DisplayName("다양한 유효한 이메일 형식 테스트(예외 없음 보장)")
    @ValueSource(strings = {
            "test@sopt.org",
            "user123@example.com",
            "first.last@company.co.kr",
            "admin+tag@domain.io"
    })
    void createEmail_WithVariousValidFormats_Success(String validEmail) {
        // when & then
        assertThatCode(() -> new Email(validEmail))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("유효하지 않은 형식은 거부")
    void rejectInvalidFormat() {
        assertThatThrownBy(() -> new Email("invalid-email"))
                .isInstanceOf(NotificationDomainException.class)
                .hasMessageContaining("유효하지 않은 이메일 형식");
    }

}
