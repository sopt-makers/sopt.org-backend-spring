package sopt.org.homepage.review.repository.command;

import org.springframework.data.jpa.repository.JpaRepository;
import sopt.org.homepage.review.domain.Review;

/**
 * 리뷰 Command Repository (쓰기 전용)
 *
 * 책임:
 * - 리뷰 생성 (INSERT)
 * - 리뷰 수정 (UPDATE)
 * - 리뷰 삭제 (DELETE)
 * - 중복 체크 등 쓰기 관련 검증
 */
public interface ReviewCommandRepository extends JpaRepository<Review, Long> {

    /**
     * URL 중복 체크- 리뷰 생성 시 URL이 이미 존재하는지 확인
     * VO 내부의 value 필드에 접근 - Spring Data JPA의 네이밍 규칙: Embedded객체_필드명
     */
    boolean existsByUrl_Value(String url);
}