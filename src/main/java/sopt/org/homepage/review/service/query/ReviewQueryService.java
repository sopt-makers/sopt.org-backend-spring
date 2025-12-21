package sopt.org.homepage.review.service.query;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.review.domain.Review;
import sopt.org.homepage.review.repository.query.ReviewQueryRepository;
import sopt.org.homepage.review.service.query.dto.ReviewSearchCond;
import sopt.org.homepage.review.service.query.dto.ReviewSummaryView;
import sopt.org.homepage.review.service.query.dto.ReviewsByAuthorView;

/**
 * 리뷰 Query Service (읽기 전용)
 * <p>
 * 책임: - 리뷰 목록 조회 - 리뷰 검색 - 통계 조회
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) //Query는 읽기 전용
public class ReviewQueryService {

    private final ReviewQueryRepository reviewQueryRepository;

    /**
     * 검색 조건에 맞는 리뷰 목록 조회
     */
    public List<ReviewSummaryView> searchReviews(ReviewSearchCond cond, long offset, int limit) {
        List<Review> reviews = reviewQueryRepository.findAllWithFilters(cond, offset, limit);

        return reviews.stream()
                .map(ReviewSummaryView::from)
                .toList();
    }

    /**
     * 검색 조건에 맞는 리뷰 총 개수 조회
     */
    public long countReviews(ReviewSearchCond cond) {
        return reviewQueryRepository.countWithFilters(cond);
    }

    /**
     * 파트별 랜덤 리뷰 조회
     */
    public List<ReviewSummaryView> getRandomReviewsByPart() {
        return Arrays.stream(PartType.values())
                .map(reviewQueryRepository::findRandomReviewByPart)
                .filter(Objects::nonNull)
                .map(ReviewSummaryView::from)
                .toList();
    }

    /**
     * 작성자별 리뷰 목록 조회
     */
    public ReviewsByAuthorView getReviewsByAuthor(String authorName) {
        List<Review> reviews = reviewQueryRepository.findAllByAuthorName(authorName);
        return ReviewsByAuthorView.from(reviews);
    }
}
