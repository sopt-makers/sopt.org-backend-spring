package sopt.org.homepage.notification.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.exception.ClientBadRequestException;

// 이메일 값 객체,JPA Embeddable로 Entity에 임베드 가능
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용 기본 생성자
public class Email {

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    @Column(name = "\"email\"", nullable = false, length = 255)
    private String value;

    public Email(String value) {
        validate(value);
        this.value = value;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

}
