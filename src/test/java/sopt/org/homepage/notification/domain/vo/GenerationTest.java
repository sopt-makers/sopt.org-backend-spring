package sopt.org.homepage.notification.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sopt.org.homepage.exception.ClientBadRequestException;

import static org.assertj.core.api.Assertions.*;

/**
 * Generation VO 단위 테스트
 */
@DisplayName("Generation VO 테스트")
class GenerationTest {

    @ParameterizedTest
    @DisplayName("유효한 기수(1~100)로 Generation VO 생성 성공")
    @ValueSource(ints = {1, 35, 50, 99, 100})
    void createGeneration_WithValidRange_Success(int validGeneration) {
        // when
        Generation generation = new Generation(validGeneration);

        // then
        assertThat(generation.getValue()).isEqualTo(validGeneration);
    }

    @Test
    @DisplayName("null 기수인 경우 예외 발생")
    void createGeneration_WithNull_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> new Generation(null))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("기수는 필수입니다");
    }

    @ParameterizedTest
    @DisplayName("1보다 작은 기수인 경우 예외 발생")
    @ValueSource(ints = {0, -1, -100})
    void createGeneration_LessThanMinimum_ThrowsException(int invalidGeneration) {
        // when & then
        assertThatThrownBy(() -> new Generation(invalidGeneration))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("기수는 1기 이상이어야 합니다");
    }

    @ParameterizedTest
    @DisplayName("100보다 큰 기수인 경우 예외 발생")
    @ValueSource(ints = {101, 200, 999})
    void createGeneration_GreaterThanMaximum_ThrowsException(int invalidGeneration) {
        // when & then
        assertThatThrownBy(() -> new Generation(invalidGeneration))
                .isInstanceOf(ClientBadRequestException.class)
                .hasMessageContaining("기수는 100기 이하여야 합니다");
    }

    @Test
    @DisplayName("같은 기수 값인지 확인")
    void isSameAs_SameValue_ReturnsTrue() {
        // given
        Generation generation = new Generation(35);

        // when & then
        assertThat(generation.isSameAs(35)).isTrue();
        assertThat(generation.isSameAs(36)).isFalse();
    }

    @Test
    @DisplayName("같은 기수 값을 가진 Generation VO는 동등하다")
    void equals_SameGenerationValue_ReturnsTrue() {
        // given
        Generation gen1 = new Generation(35);
        Generation gen2 = new Generation(35);

        // when & then
        assertThat(gen1).isEqualTo(gen2);
        assertThat(gen1.hashCode()).isEqualTo(gen2.hashCode());
    }
}