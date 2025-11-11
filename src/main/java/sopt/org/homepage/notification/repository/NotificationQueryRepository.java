package sopt.org.homepage.notification.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Generation;

/**
 * Notification Query Repository - 읽기 작업만 담당
 */
@Repository
public interface NotificationQueryRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByGeneration(Generation generation);

}
