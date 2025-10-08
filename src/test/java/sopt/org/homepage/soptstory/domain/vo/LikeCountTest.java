package sopt.org.homepage.soptstory.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * LikeCount Value Object 단위 테스트
 *
 * 핵심 테스트: 좋아요 증가/감소 비즈니스 규칙
 */
@DisplayName("LikeCount 단위 테스트")
class LikeCountTest {

    @Test
    @DisplayName("초기값 0으로 생성 성공")
    void createInitial() {
        // when
        LikeCount count = LikeCount.initial();

        // then
        assertThat(count.getValue()).isZero();
    }

    @Test
    @DisplayName("특정 값으로 생성 성공")
    void createWithValue() {
        // given
        int value = 42;

        // when
        LikeCount count = new LikeCount(value);

        // then
        assertThat(count.getValue()).isEqualTo(value);
    }

    @Test
    @DisplayName("음수 값으로 생성 시도하면 예외 발생")
    void createWithNegativeValue() {
        // when & then
        assertThatThrownBy(() -> new LikeCount(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("좋아요 개수는 0 이상이어야 합니다.");
    }

    @Test
    @DisplayName("좋아요 증가 성공")
    void increment() {
        // given
        LikeCount count = new LikeCount(5);

        // when
        LikeCount incremented = count.increment();

        // then
        assertThat(incremented.getValue()).isEqualTo(6);
        // 원본은 불변
        assertThat(count.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("0에서 좋아요 증가 성공")
    void incrementFromZero() {
        // given
        LikeCount count = LikeCount.initial();

        // when
        LikeCount incremented = count.increment();

        // then
        assertThat(incremented.getValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("최대값에서 좋아요 증가 시도하면 예외 발생")
    void incrementAtMaxValue() {
        // given
        LikeCount count = new LikeCount(Integer.MAX_VALUE);

        // when & then
        assertThatThrownBy(() -> count.increment())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("좋아요 개수가 최대값에 도달했습니다.");
    }

    @Test
    @DisplayName("좋아요 감소 성공")
    void decrement() {
        // given
        LikeCount count = new LikeCount(5);

        // when
        LikeCount decremented = count.decrement();

        // then
        assertThat(decremented.getValue()).isEqualTo(4);
        // 원본은 불변
        assertThat(count.getValue()).isEqualTo(5);
    }

    @Test
    @DisplayName("1에서 좋아요 감소하면 0이 된다")
    void decrementToZero() {
        // given
        LikeCount count = new LikeCount(1);

        // when
        LikeCount decremented = count.decrement();

        // then
        assertThat(decremented.getValue()).isZero();
    }

    @Test
    @DisplayName("0에서 좋아요 감소 시도하면 예외 발생")
    void decrementAtZero() {
        // given
        LikeCount count = LikeCount.initial();

        // when & then
        assertThatThrownBy(() -> count.decrement())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("좋아요 개수는 음수가 될 수 없습니다.");
    }

    @Test
    @DisplayName("여러 번 증가 후 감소 시나리오")
    void multipleOperations() {
        // given
        LikeCount count = LikeCount.initial();

        // when: 3번 증가
        count = count.increment();
        count = count.increment();
        count = count.increment();

        // then: 3이 됨
        assertThat(count.getValue()).isEqualTo(3);

        // when: 1번 감소
        count = count.decrement();

        // then: 2가 됨
        assertThat(count.getValue()).isEqualTo(2);
    }

    @Test
    @DisplayName("isZero() - 0이면 true 반환")
    void isZeroWhenZero() {
        // given
        LikeCount count = LikeCount.initial();

        // when & then
        assertThat(count.isZero()).isTrue();
    }

    @Test
    @DisplayName("isZero() - 0이 아니면 false 반환")
    void isZeroWhenNotZero() {
        // given
        LikeCount count = new LikeCount(5);

        // when & then
        assertThat(count.isZero()).isFalse();
    }

    @Test
    @DisplayName("isGreaterThan() - 특정 값보다 크면 true 반환")
    void isGreaterThanWhenGreater() {
        // given
        LikeCount count = new LikeCount(10);

        // when & then
        assertThat(count.isGreaterThan(5)).isTrue();
    }

    @Test
    @DisplayName("isGreaterThan() - 특정 값보다 작거나 같으면 false 반환")
    void isGreaterThanWhenNotGreater() {
        // given
        LikeCount count = new LikeCount(5);

        // when & then
        assertThat(count.isGreaterThan(5)).isFalse();
        assertThat(count.isGreaterThan(10)).isFalse();
    }

    @Test
    @DisplayName("동일한 값의 VO는 동등하다")
    void equalsSameValue() {
        // given
        LikeCount count1 = new LikeCount(10);
        LikeCount count2 = new LikeCount(10);

        // when & then
        assertThat(count1).isEqualTo(count2);
        assertThat(count1.hashCode()).isEqualTo(count2.hashCode());
    }

    @Test
    @DisplayName("다른 값의 VO는 동등하지 않다")
    void notEqualsDifferentValue() {
        // given
        LikeCount count1 = new LikeCount(10);
        LikeCount count2 = new LikeCount(20);

        // when & then
        assertThat(count1).isNotEqualTo(count2);
    }

    @Test
    @DisplayName("toString()은 숫자 문자열을 반환한다")
    void toStringReturnsNumber() {
        // given
        LikeCount count = new LikeCount(42);

        // when & then
        assertThat(count.toString()).isEqualTo("42");
    }
}