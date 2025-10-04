package sopt.org.homepage.review.repository.query;

import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.review.domain.Review;
import sopt.org.homepage.review.service.query.dto.ReviewSearchCond;

import java.util.List;

/**
 * 리뷰 Query Repository (읽기 전용)
 *
 * 책임:
 * - 필터링을 통한 리뷰 목록 조회
 * - 통계 조회
 * - 복잡한 검색 쿼리
 */
public interface ReviewQueryRepository {

    /**
     * 검색 조건에 맞는 리뷰 목록 조회
     *
     * @param cond 검색 조건 (카테고리, 파트, 기수, 활동 등)
     * @param offset 페이지네이션 오프셋
     * @param limit 페이지 크기
     * @return 리뷰 목록
     */
    List<Review> findAllWithFilters(ReviewSearchCond cond, long offset, int limit);

    /**
     * 검색 조건에 맞는 리뷰 개수 조회
     *
     * @param cond 검색 조건
     * @return 총 개수
     */
    long countWithFilters(ReviewSearchCond cond);

    /**
     * 파트별 랜덤 리뷰 조회
     *
     * @param part 파트
     * @return 랜덤 리뷰 (없으면 null)
     */
    Review findRandomReviewByPart(Part part);

    /**
     * 작성자명으로 리뷰 목록 조회
     *
     * @param authorName 작성자명
     * @return 해당 작성자의 모든 리뷰
     */
    List<Review> findAllByAuthorName(String authorName);
}