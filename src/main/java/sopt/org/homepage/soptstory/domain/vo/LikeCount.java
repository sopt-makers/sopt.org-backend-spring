package sopt.org.homepage.soptstory.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * 좋아요 개수 Value Object
 *
 * 책임:
 * - 좋아요 개수의 비즈니스 규칙 관리
 * - 증가/감소 연산
 * - 음수 방지 (불변 조건)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class LikeCount {

    private static final int MIN_COUNT = 0;
    private static final int MAX_COUNT = Integer.MAX_VALUE;

    @Column(name = "\"likeCount\"", nullable = false)
    private int value;

    /**
     * 좋아요 개수 생성
     *
     * @param value 초기 값 (기본값: 0)
     * @throws IllegalArgumentException 값이 음수인 경우
     */
    public LikeCount(int value) {
        validate(value);
        this.value = value;
    }

    /**
     * 기본 생성 (초기값 0)
     */
    public static LikeCount initial() {
        return new LikeCount(0);
    }

    /**
     * 좋아요 증가
     *
     * @return 증가된 새로운 LikeCount 인스턴스
     * @throws IllegalStateException 최대값 도달 시
     */
    public LikeCount increment() {
        if (value >= MAX_COUNT) {
            throw new IllegalStateException(
                    "좋아요 개수가 최대값에 도달했습니다."
            );
        }
        return new LikeCount(this.value + 1);
    }

    /**
     * 좋아요 감소
     *
     * @return 감소된 새로운 LikeCount 인스턴스
     * @throws IllegalStateException 이미 0인 경우
     */
    public LikeCount decrement() {
        if (value <= MIN_COUNT) {
            throw new IllegalStateException(
                    "좋아요 개수는 음수가 될 수 없습니다."
            );
        }
        return new LikeCount(this.value - 1);
    }

    private void validate(int value) {
        if (value < MIN_COUNT) {
            throw new IllegalArgumentException(
                    "좋아요 개수는 0 이상이어야 합니다."
            );
        }
    }

    /**
     * 0인지 확인
     */
    public boolean isZero() {
        return value == 0;
    }

    /**
     * 특정 값보다 큰지 확인
     */
    public boolean isGreaterThan(int target) {
        return value > target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LikeCount likeCount = (LikeCount) o;
        return value == likeCount.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}