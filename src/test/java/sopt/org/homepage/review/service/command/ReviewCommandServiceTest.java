package sopt.org.homepage.review.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.review.domain.Review;
import sopt.org.homepage.review.domain.vo.CategoryType;
import sopt.org.homepage.review.exception.DuplicateReviewUrlException;
import sopt.org.homepage.review.exception.InvalidReviewSubjectException;
import sopt.org.homepage.review.repository.command.ReviewCommandRepository;
import sopt.org.homepage.review.service.command.dto.CreateReviewCommand;

/**
 * ReviewCommandService 통합 테스트
 * <p>
 * 테스트 전략: - 실제 DB 사용 (TestContainer) - @Transactional + @AfterEach로 테스트 격리 - Mock 없이 실제 동작 검증 - 비즈니스 규칙이 도메인에서 작동하는지
 * 확인
 */
@DisplayName("ReviewCommandService 통합 테스트")
@Transactional
class ReviewCommandServiceTest extends IntegrationTestBase {

    @Autowired
    private ReviewCommandService commandService;

    @Autowired
    private ReviewCommandRepository commandRepository;

    @AfterEach
    void tearDown() {
        commandRepository.deleteAll();
    }

    // === 리뷰 생성 성공 케이스 ===

    @Test
    @DisplayName("유효한 Command로 리뷰 생성 성공 - 전체 활동")
    void createReview_WithActivityCategory_Success() {
        // given
        CreateReviewCommand command = createValidCommand(
                CategoryType.ACTIVITY.getDisplayName(),
                List.of("세미나", "프로젝트")
        );

        // when
        Long reviewId = commandService.createReview(command);

        // then
        assertThat(reviewId).isNotNull();

        // DB 검증
        Review saved = commandRepository.findById(reviewId).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("SOPT 34기 활동 후기");
        assertThat(saved.getDescription()).isEqualTo("정말 유익한 경험이었습니다.");
        assertThat(saved.getGeneration()).isEqualTo(34);
        assertThat(saved.getPartType()).isEqualTo(PartType.SERVER);
        assertThat(saved.getCategoryType()).isEqualTo(CategoryType.ACTIVITY);
        assertThat(saved.getSubjectValues())
                .containsExactly("세미나", "프로젝트");
    }

    @Test
    @DisplayName("유효한 Command로 리뷰 생성 성공 - 서류/면접")
    void createReview_WithRecruitingCategory_Success() {
        // given
        CreateReviewCommand command = createValidCommand(
                CategoryType.RECRUITING.getDisplayName(),
                List.of("서류", "1차 면접")
        );

        // when
        Long reviewId = commandService.createReview(command);

        // then
        assertThat(reviewId).isNotNull();

        // DB 검증
        Review saved = commandRepository.findById(reviewId).orElseThrow();
        assertThat(saved.getCategoryType()).isEqualTo(CategoryType.RECRUITING);
        assertThat(saved.getSubjectValues()).containsExactly("서류", "1차 면접");
    }

    @Test
    @DisplayName("유효한 Command로 리뷰 생성 성공 - 기타 카테고리 (세부 주제 없음)")
    void createReview_WithOtherCategory_Success() {
        // given
        CreateReviewCommand command = createValidCommand(
                CategoryType.SEMINAR.getDisplayName(),
                List.of()
        );

        // when
        Long reviewId = commandService.createReview(command);

        // then
        assertThat(reviewId).isNotNull();

        // DB 검증
        Review saved = commandRepository.findById(reviewId).orElseThrow();
        assertThat(saved.getCategoryType()).isEqualTo(CategoryType.SEMINAR);
        assertThat(saved.getSubjectValues()).isEmpty();
    }

    // === 비즈니스 규칙 검증 (도메인 로직) ===

    @Test
    @DisplayName("전체 활동 카테고리인데 세부 활동이 없으면 예외 발생")
    void createReview_ActivityCategoryWithoutSubjects_ThrowsException() {
        // given
        CreateReviewCommand command = createValidCommand(
                CategoryType.ACTIVITY.getDisplayName(),
                List.of()  // 빈 세부 활동
        );

        // when & then
        assertThatThrownBy(() -> commandService.createReview(command))
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessage("전체활동 카테고리는 세부 활동이 필수입니다.");
    }

    @Test
    @DisplayName("서류/면접 카테고리인데 세부 유형이 없으면 예외 발생")
    void createReview_RecruitingCategoryWithoutSubjects_ThrowsException() {
        // given
        CreateReviewCommand command = createValidCommand(
                CategoryType.RECRUITING.getDisplayName(),
                List.of()  // 빈 세부 유형
        );

        // when & then
        assertThatThrownBy(() -> commandService.createReview(command))
                .isInstanceOf(InvalidReviewSubjectException.class)
                .hasMessage("서류/면접 카테고리는 세부 유형이 필수입니다.");
    }

    @Test
    @DisplayName("중복된 URL로 리뷰 생성 시 예외 발생")
    void createReview_WithDuplicateUrl_ThrowsException() {
        // given
        CreateReviewCommand firstCommand = createValidCommand(
                CategoryType.SEMINAR.getDisplayName(),
                List.of()
        );
        commandService.createReview(firstCommand);

        // 같은 URL로 두 번째 리뷰 시도
        CreateReviewCommand duplicateCommand = createValidCommand(
                CategoryType.SEMINAR.getDisplayName(),
                List.of()
        );

        // when & then
        assertThatThrownBy(() -> commandService.createReview(duplicateCommand))
                .isInstanceOf(DuplicateReviewUrlException.class)
                .hasMessage("이미 등록된 활동후기입니다.");
    }

    // === 헬퍼 메서드 ===

    private CreateReviewCommand createValidCommand(
            String category,
            List<String> subjects
    ) {
        return new CreateReviewCommand(
                "SOPT 34기 활동 후기",
                "정말 유익한 경험이었습니다.",
                "https://example.com/thumbnail.jpg",
                "Medium",
                "https://medium.com/@sopt/review-unique",
                category,
                subjects,
                "홍길동",
                "https://example.com/profile.jpg",
                34,
                PartType.SERVER
        );
    }
}
