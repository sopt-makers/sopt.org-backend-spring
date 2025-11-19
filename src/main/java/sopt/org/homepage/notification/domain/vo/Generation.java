package sopt.org.homepage.notification.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.notification.exception.NotificationDomainException;

/**
 * 기수 값 객체 - SOPT 기수는 양수
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA용
public class Generation {

    @Column(name = "\"generation\"", nullable = false)
    private Integer value;

    public Generation(Integer value) {
        validate(value);
        this.value = value;
    }

    private void validate(Integer value) {
        if (value <= 0) {
            throw NotificationDomainException.generationNotPositive(value);
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
