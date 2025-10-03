package sopt.org.homepage.notification.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;

import java.time.LocalDateTime;

/**
 * 모집 알림 신청 도메인 엔티티
 * Rich Domain Model: 비즈니스 로직을 스스로 처리
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

    /**
     * 팩토리 메서드: Notification 생성
     * - 도메인 규칙: 이메일과 기수는 필수
     * - VO에서 각각 검증 완료된 상태
     *
     * @param email 알림 받을 이메일
     * @param generation 알림 신청 기수
     * @return 생성된 Notification
     */
    public static Notification create(Email email, Generation generation) {
        validateCreation(email, generation);
        return new Notification(email, generation);
    }

    /**
     * private 생성자 - 팩토리 메서드를 통해서만 생성 가능
     */
    private Notification(Email email, Generation generation) {
        this.email = email;
        this.generation = generation;
    }

    /**
     * 생성 규칙 검증
     */
    private static void validateCreation(Email email, Generation generation) {
        if (email == null) {
            throw new BusinessLogicException("이메일은 필수입니다");
        }
        if (generation == null) {
            throw new BusinessLogicException("기수는 필수입니다");
        }
    }

    /**
     * 특정 기수와 이메일 조합인지 확인
     * - 중복 검사에 사용
     */
    public boolean isSameEmailAndGeneration(Email email, Generation generation) {
        return this.email.equals(email) &&
                this.generation.equals(generation);
    }

    /**
     * 도메인 정보를 문자열로 표현 (로깅용)
     */
    @Override
    public String toString() {
        return String.format(
                "Notification(id=%d, email=%s, generation=%s)",
                id, email, generation
        );
    }
}