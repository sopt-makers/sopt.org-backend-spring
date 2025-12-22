package sopt.org.homepage.notification;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

/**
 * 모집 알림 신청 엔티티
 * <p>
 * 비즈니스 규칙: - 이메일 형식 검증 (@Email) - 기수는 1 이상의 양수 (@Min) - 동일 이메일+기수 조합 중복 불가 (Service에서 검증)
 */
@Entity
@Table(name = "\"Notification\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"")
    private Long id;


    @Column(name = "\"email\"", nullable = false)
    private String email;

    @Column(name = "\"generation\"", nullable = false)
    private Integer generation;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Notification of(String email, Integer generation) {
        Notification notification = new Notification();
        notification.email = email;
        notification.generation = generation;
        return notification;
    }

    // private 생성자 (JPA + 팩토리 메서드용)
    private Notification(String email, Integer generation) {
        this.email = email;
        this.generation = generation;
    }

}
