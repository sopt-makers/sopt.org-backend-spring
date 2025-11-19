package sopt.org.homepage.notification.domain.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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


}
