package sopt.org.homepage.notification.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import sopt.org.homepage.exception.ClientBadRequestException;

import static org.assertj.core.api.Assertions.*;

/**
 * Email VO 단위 테스트
 * - Mock 없이 순수 Java 객체 테스트
 * - 검증 로직 집중 테스트
 */
@DisplayName("Email VO 테스트")
class EmailTest {

    @Test// 단일 케이스에 대해, 예외 여부뿐 아니라 내부 상태까지 검증.
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
    })//“이런 이메일 형식들은 다 유효해야 한다”라는 포괄적인 보증을 만들 때 쓰는 패턴
    void createEmail_WithVariousValidFormats_Success(String validEmail) {
        // when & then
        assertThatCode(() -> new Email(validEmail))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   "})
    @DisplayName("null이거나 빈 이메일인 경우 예외 발생")
    void createEmail_WithNullOrEmpty_ThrowsException(String invalidEmail) {
        // when & then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("이메일은 필수입니다");
    }

    @ParameterizedTest
    @DisplayName("잘못된 이메일 형식인 경우 예외 발생")
    @ValueSource(strings = {
            "invalid-email",           // @ 없음
            "@sopt.org",               // 로컬 파트 없음
            "test@",                   // 도메인 없음
            "test@domain",             // TLD 없음
            "test@@sopt.org",          // @ 중복
            "test @sopt.org",          // 공백 포함
            "test@sopt .org"           // 도메인에 공백
    })
    void createEmail_WithInvalidFormat_ThrowsException(String invalidEmail) {
        // when & then
        assertThatThrownBy(() -> new Email(invalidEmail))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("유효하지 않은 이메일 형식입니다");
    }

    @Test
    @DisplayName("같은 이메일 값을 가진 Email VO는 동등하다")
    void equals_SameEmailValue_ReturnsTrue() {
        // given
        Email email1 = new Email("test@sopt.org");
        Email email2 = new Email("test@sopt.org");

        // when & then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    @DisplayName("다른 이메일 값을 가진 Email VO는 동등하지 않다")
    void equals_DifferentEmailValue_ReturnsFalse() {
        // given
        Email email1 = new Email("test1@sopt.org");
        Email email2 = new Email("test2@sopt.org");

        // when & then
        assertThat(email1).isNotEqualTo(email2);
    }
}