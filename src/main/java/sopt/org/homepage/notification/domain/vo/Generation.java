package sopt.org.homepage.notification.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.exception.ClientBadRequestException;

import java.util.Objects;

/**
 * 기수 값 객체
 * - SOPT 기수는 1기부터 시작
 * - 유효한 범위 검증 (1 ~ 100)
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용
public class Generation {

    private static final int MIN_GENERATION = 1;
    private static final int MAX_GENERATION = 100; // 합리적인 상한선

    @Column(name = "\"generation\"", nullable = false)
    private Integer value;

    /**
     * Generation 생성 (검증 포함)
     * @param value 기수 번호
     * @throws ClientBadRequestException 유효하지 않은 기수인 경우
     */
    public Generation(Integer value) {
        validate(value);
        this.value = value;
    }

    /**
     * 기수 유효성 검증
     */
    private void validate(Integer value) {
        if (value == null) {
            throw new ClientBadRequestException("기수는 필수입니다");
        }

        if (value < MIN_GENERATION) {
            throw new ClientBadRequestException(
                    "기수는 " + MIN_GENERATION + "기 이상이어야 합니다"
            );
        }

        if (value > MAX_GENERATION) {
            throw new ClientBadRequestException(
                    "기수는 " + MAX_GENERATION + "기 이하여야 합니다"
            );
        }
    }

    /**
     * 기수가 특정 값과 같은지 확인
     */
    public boolean isSameAs(Integer otherValue) {
        return this.value.equals(otherValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Generation that = (Generation) o;
        return Objects.equals(value, that.value);
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