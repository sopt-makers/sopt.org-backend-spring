package sopt.org.homepage.notification;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Notification Repository
 * <p>
 * Command + Query 통합
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * 이메일과 기수로 알림 조회
     */
    Optional<Notification> findByEmailAndGeneration(String email, Integer generation);

    /**
     * 이메일과 기수 조합 존재 여부 (중복 체크용)
     */
    boolean existsByEmailAndGeneration(String email, Integer generation);

    /**
     * 특정 기수의 모든 알림 조회
     */
    List<Notification> findByGeneration(Integer generation);
}
