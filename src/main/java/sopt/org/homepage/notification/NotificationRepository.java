package sopt.org.homepage.notification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Integer> {
    NotificationEntity findByEmailAndGeneration(String email, int generation);

    List<NotificationEntity> findByGeneration(int generation);
}
