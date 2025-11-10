package sopt.org.homepage.notification.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.exception.ClientBadRequestException;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 이메일 값 객체
 * - 불변 객체로 설계
 * - 이메일 형식 검증 포함
 * - JPA Embeddable로 Entity에 임베드 가능
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Column(name = "\"email\"", nullable = false, length = 255)
    private String value;

    /**
     * Email 생성 (검증 포함)
     * @param value 이메일 문자열
     * @throws ClientBadRequestException 이메일 형식이 유효하지 않은 경우
     */
    public Email(String value) {
        validate(value);
        this.value = value;
    }

    /**
     * 이메일 형식 검증
     */
    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new ClientBadRequestException("이메일은 필수입니다");
        }

        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new ClientBadRequestException(
                    "유효하지 않은 이메일 형식입니다: " + value
            );
        }
    }

    /**
     * Value Object는 값 기반 동등성 비교
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}