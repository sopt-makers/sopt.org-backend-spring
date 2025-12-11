package sopt.org.homepage.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;
import sopt.org.homepage.notification.exception.NotificationDomainException;

/**
 * 모집 알림 신청 도메인 엔티티 Rich Domain Model: 비즈니스 로직을 스스로 처리
 */
@Entity
@Table(
        name = "\"Notification\"",
        uniqueConstraints = {// 같은 이메일이 같은 기수에 대해 두 번 알림을 신청할 수 없게 됨.
                @UniqueConstraint(
                        name = "uk_notification_email_generation",
                        columnNames = {"email", "generation"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA + 도메인 캡슐화
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Generation generation;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Notification create(Email email, Generation generation) {
        validateCreation(email, generation);
        return new Notification(email, generation);
    }

    private Notification(Email email, Generation generation) {
        this.email = email;
        this.generation = generation;
    }

    // 생성 규칙 검증 VO는 내부 값만 검증하므로, Entity는 VO 참조 자체의 null을 검증
    private static void validateCreation(Email email, Generation generation) {
        if (email == null) {
            throw NotificationDomainException.emailRequired();
        }
        if (generation == null) {
            throw NotificationDomainException.generationRequired();
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Notification(id=%d, email=%s, generation=%s)",
                id, email, generation
        );
    }
}
