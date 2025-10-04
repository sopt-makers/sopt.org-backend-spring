package sopt.org.homepage.review.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.review.domain.Review;
import sopt.org.homepage.review.domain.vo.*;
import sopt.org.homepage.review.repository.command.ReviewCommandRepository;
import sopt.org.homepage.review.service.command.dto.CreateReviewCommand;

import java.util.List;

/**
 * 리뷰 Command Service (쓰기 전용)
 *
 * 책임:
 * - 리뷰 생성
 * - 리뷰 수정 (향후)
 * - 리뷰 삭제 (향후)
 * 특징:
 * - 얇은 서비스 계층 (비즈니스 로직은 도메인에 위임)
 * - 트랜잭션 관리
 * - 외부 의존성 조율 (Repository, 외부 서비스 등)
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ReviewCommandService {

    private final ReviewCommandRepository reviewCommandRepository;

    /**
     * 리뷰 생성
     *
     * 1. URL 중복 체크
     * 2. 도메인 객체 생성 (팩토리 메서드 활용)
     * 3. 저장
     *
     * @param command 리뷰 생성 커맨드
     * @return 생성된 리뷰 ID
     */
    public Long createReview(CreateReviewCommand command) {
        // 1. URL 중복 체크
        boolean isDuplicate = reviewCommandRepository.existsByUrl_Value(command.url());

        // 2. VO 생성
        ReviewContent content = new ReviewContent(
                command.title(),
                command.description(),
                command.thumbnailUrl(),
                command.platform()
        );

        ReviewAuthor author = new ReviewAuthor(
                command.authorName(),
                command.authorProfileImageUrl()
        );

        ReviewCategory category = new ReviewCategory(command.category());

        ReviewSubjects subjects = new ReviewSubjects(
                command.subjects() != null ? command.subjects() : List.of()
        );

        ReviewUrl url = new ReviewUrl(command.url());

        // 3. 도메인 객체 생성 (팩토리 메서드)
        Review review = Review.create(
                content,
                author,
                command.generation(),
                command.part(),
                category,
                subjects,
                url
        );

        // 4. URL 중복 검증
        review.validateUrlUniqueness(isDuplicate);

        // 5. 저장
        Review savedReview = reviewCommandRepository.save(review);

        return savedReview.getId();
    }
}