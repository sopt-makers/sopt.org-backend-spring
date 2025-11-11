package sopt.org.homepage.notification.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sopt.org.homepage.notification.domain.Notification;
import sopt.org.homepage.notification.domain.vo.Email;
import sopt.org.homepage.notification.domain.vo.Generation;

import java.util.Optional;

/**
 * Notification Command Repository
 * - 쓰기 작업만 담당 (등록, 수정, 삭제)
 * - Spring Data JPA 기본 메서드 활용
 */
@Repository
public interface NotificationCommandRepository extends JpaRepository<Notification, Long> {

    /**
     * 이메일과 기수로 Notification 조회
     * - 중복 검사용
     *
     * @param email 이메일 VO
     * @param generation 기수 VO
     * @return 조회된 Notification (없으면 empty)
     */
    Optional<Notification> findByEmailAndGeneration(Email email, Generation generation);

    /**
     * 이메일과 기수 조합의 존재 여부 확인
     * - 중복 검사 최적화 (count 쿼리 대신 exists 사용)
     *
     * @param email 이메일 VO
     * @param generation 기수 VO
     * @return 존재 여부
     */
    boolean existsByEmailAndGeneration(Email email, Generation generation);
}