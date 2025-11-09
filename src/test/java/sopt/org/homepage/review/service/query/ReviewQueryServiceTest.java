package sopt.org.homepage.review.service.query;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.review.domain.Review;
import sopt.org.homepage.review.domain.vo.*;
import sopt.org.homepage.review.repository.command.ReviewCommandRepository;
import sopt.org.homepage.review.service.query.dto.ReviewSearchCond;
import sopt.org.homepage.review.service.query.dto.ReviewSummaryView;
import sopt.org.homepage.review.service.query.dto.ReviewsByAuthorView;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * ReviewQueryService 통합 테스트
 *
 * 테스트 전략:
 * - 실제 DB 사용 (TestContainer)
 * - @Transactional + @AfterEach로 테스트 격리
 * - Mock 없이 실제 동작 검증
 * - 다양한 검색 조건과 필터링 로직 검증
 */
@DisplayName("ReviewQueryService 통합 테스트")
@Transactional
class ReviewQueryServiceTest extends IntegrationTestBase {

    @Autowired
    private ReviewQueryService queryService;

    @Autowired
    private ReviewCommandRepository commandRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 준비
        commandRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        commandRepository.deleteAll();
    }

    // === 리뷰 검색 테스트 ===

    @Test
    @DisplayName("조건 없이 모든 리뷰 조회")
    void searchReviews_WithNoConditions_ReturnsAll() {
        // given
        Review review1 = createAndSaveReview("리뷰1", CategoryType.ACTIVITY, List.of("세미나"), PartType.SERVER, 34);
        Review review2 = createAndSaveReview("리뷰2", CategoryType.SEMINAR, List.of(), PartType.DESIGN, 33);
        Review review3 = createAndSaveReview("리뷰3", CategoryType.RECRUITING, List.of("서류"), PartType.WEB, 34);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, null, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(3);
        assertThat(results)
                .extracting(ReviewSummaryView::title)
                .containsExactlyInAnyOrder("리뷰1", "리뷰2", "리뷰3");
    }

    @Test
    @DisplayName("카테고리로 필터링 - 전체 활동")
    void searchReviews_FilterByActivityCategory() {
        // given
        createAndSaveReview("활동 리뷰", CategoryType.ACTIVITY, List.of("세미나"), PartType.SERVER, 34);
        createAndSaveReview("세미나 리뷰", CategoryType.SEMINAR, List.of(), PartType.DESIGN, 34);
        createAndSaveReview("프로젝트 리뷰", CategoryType.PROJECT, List.of(), PartType.WEB, 34);

        ReviewSearchCond cond = new ReviewSearchCond("전체 활동", null, null, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("활동 리뷰");
    }

    @Test
    @DisplayName("카테고리로 필터링 - 서류/면접")
    void searchReviews_FilterByRecruitingCategory() {
        // given
        createAndSaveReview("서류 후기", CategoryType.RECRUITING, List.of("서류"), PartType.SERVER, 34);
        createAndSaveReview("면접 후기", CategoryType.RECRUITING, List.of("면접"), PartType.DESIGN, 34);
        createAndSaveReview("활동 후기", CategoryType.ACTIVITY, List.of("세미나"), PartType.WEB, 34);

        ReviewSearchCond cond = new ReviewSearchCond("서류/면접", null, null, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(ReviewSummaryView::title)
                .containsExactlyInAnyOrder("서류 후기", "면접 후기");
    }

    @Test
    @DisplayName("세부 활동으로 필터링 - 세미나")
    void searchReviews_FilterBySubActivity_Seminar() {
        // given
        createAndSaveReview("세미나 리뷰", CategoryType.ACTIVITY, List.of("세미나"), PartType.SERVER, 34);
        createAndSaveReview("솝텀 리뷰", CategoryType.ACTIVITY, List.of("솝텀"), PartType.DESIGN, 34);
        createAndSaveReview("세미나+솝텀", CategoryType.ACTIVITY, List.of("세미나", "솝텀"), PartType.WEB, 34);

        ReviewSearchCond cond = new ReviewSearchCond("전체 활동", "세미나", null, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(ReviewSummaryView::title)
                .containsExactlyInAnyOrder("세미나 리뷰", "세미나+솝텀");
    }

    @Test
    @DisplayName("세부 활동으로 필터링 - 솝텀")
    void searchReviews_FilterBySubActivity_Sopterm() {
        // given
        createAndSaveReview("세미나 리뷰", CategoryType.ACTIVITY, List.of("세미나"), PartType.SERVER, 34);
        createAndSaveReview("솝텀 리뷰", CategoryType.ACTIVITY, List.of("솝텀"), PartType.DESIGN, 34);
        createAndSaveReview("세미나+솝텀", CategoryType.ACTIVITY, List.of("세미나", "솝텀"), PartType.WEB, 34);

        ReviewSearchCond cond = new ReviewSearchCond("전체 활동", "솝텀", null, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(ReviewSummaryView::title)
                .containsExactlyInAnyOrder("솝텀 리뷰", "세미나+솝텀");
    }

    @Test
    @DisplayName("파트로 필터링")
    void searchReviews_FilterByPart() {
        // given
        createAndSaveReview("서버 리뷰1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("서버 리뷰2", CategoryType.PROJECT, List.of(), PartType.SERVER, 33);
        createAndSaveReview("디자인 리뷰", CategoryType.SEMINAR, List.of(), PartType.DESIGN, 34);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, PartType.SERVER, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(ReviewSummaryView::title)
                .containsExactlyInAnyOrder("서버 리뷰1", "서버 리뷰2");
    }

    @Test
    @DisplayName("기수로 필터링")
    void searchReviews_FilterByGeneration() {
        // given
        createAndSaveReview("34기 리뷰1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("34기 리뷰2", CategoryType.PROJECT, List.of(), PartType.DESIGN, 34);
        createAndSaveReview("33기 리뷰", CategoryType.SEMINAR, List.of(), PartType.WEB, 33);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, null, 34);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(ReviewSummaryView::title)
                .containsExactlyInAnyOrder("34기 리뷰1", "34기 리뷰2");
    }

    @Test
    @DisplayName("복합 조건으로 필터링 - 카테고리 + 파트")
    void searchReviews_FilterByMultipleConditions_CategoryAndPart() {
        // given
        createAndSaveReview("활동_서버", CategoryType.ACTIVITY, List.of("세미나"), PartType.SERVER, 34);
        createAndSaveReview("활동_디자인", CategoryType.ACTIVITY, List.of("세미나"), PartType.DESIGN, 34);
        createAndSaveReview("세미나_서버", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);

        ReviewSearchCond cond = new ReviewSearchCond("전체 활동", null, PartType.SERVER, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("활동_서버");
    }

    @Test
    @DisplayName("복합 조건으로 필터링 - 세부활동 + 파트 + 기수")
    void searchReviews_FilterByMultipleConditions_All() {
        // given
        createAndSaveReview("목표", CategoryType.ACTIVITY, List.of("세미나"), PartType.SERVER, 34);
        createAndSaveReview("제외1", CategoryType.ACTIVITY, List.of("솝텀"), PartType.SERVER, 34);
        createAndSaveReview("제외2", CategoryType.ACTIVITY, List.of("세미나"), PartType.DESIGN, 34);
        createAndSaveReview("제외3", CategoryType.ACTIVITY, List.of("세미나"), PartType.SERVER, 33);

        ReviewSearchCond cond = new ReviewSearchCond("전체 활동", "세미나", PartType.SERVER, 34);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).title()).isEqualTo("목표");
    }

    @Test
    @DisplayName("검색 결과가 없는 경우 빈 리스트 반환")
    void searchReviews_NoResults_ReturnsEmptyList() {
        // given
        createAndSaveReview("리뷰1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, PartType.DESIGN, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("페이지네이션 - offset과 limit 적용")
    void searchReviews_WithPagination() {
        // given - 5개의 리뷰 생성
        createAndSaveReview("리뷰1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("리뷰2", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("리뷰3", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("리뷰4", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("리뷰5", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, null, null);

        // when - 2페이지 조회 (offset=2, limit=2)
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 2, 2);

        // then
        assertThat(results).hasSize(2);
    }

    // === 리뷰 개수 조회 테스트 ===

    @Test
    @DisplayName("조건 없이 전체 리뷰 개수 조회")
    void countReviews_WithNoConditions_ReturnsTotal() {
        // given
        createAndSaveReview("리뷰1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("리뷰2", CategoryType.PROJECT, List.of(), PartType.DESIGN, 33);
        createAndSaveReview("리뷰3", CategoryType.ACTIVITY, List.of("세미나"), PartType.WEB, 34);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, null, null);

        // when
        long count = queryService.countReviews(cond);

        // then
        assertThat(count).isEqualTo(3);
    }

    @Test
    @DisplayName("필터링 조건에 맞는 리뷰 개수 조회")
    void countReviews_WithConditions_ReturnsFilteredCount() {
        // given
        createAndSaveReview("34기_서버1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("34기_서버2", CategoryType.PROJECT, List.of(), PartType.SERVER, 34);
        createAndSaveReview("34기_디자인", CategoryType.SEMINAR, List.of(), PartType.DESIGN, 34);
        createAndSaveReview("33기_서버", CategoryType.SEMINAR, List.of(), PartType.SERVER, 33);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, PartType.SERVER, 34);

        // when
        long count = queryService.countReviews(cond);

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("검색 결과가 없는 경우 0 반환")
    void countReviews_NoResults_ReturnsZero() {
        // given
        createAndSaveReview("리뷰1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);

        ReviewSearchCond cond = new ReviewSearchCond(null, null, PartType.DESIGN, null);

        // when
        long count = queryService.countReviews(cond);

        // then
        assertThat(count).isZero();
    }

    // === 파트별 랜덤 리뷰 조회 테스트 ===

    @Test
    @DisplayName("파트별 랜덤 리뷰 조회 - 각 파트당 1개씩")
    void getRandomReviewsByPart_ReturnsOnePerPart() {
        // given - 각 파트별로 2개씩 생성
        createAndSaveReview("서버1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("서버2", CategoryType.PROJECT, List.of(), PartType.SERVER, 33);
        createAndSaveReview("디자인1", CategoryType.SEMINAR, List.of(), PartType.DESIGN, 34);
        createAndSaveReview("디자인2", CategoryType.PROJECT, List.of(), PartType.DESIGN, 33);
        createAndSaveReview("웹1", CategoryType.SEMINAR, List.of(), PartType.WEB, 34);
        createAndSaveReview("웹2", CategoryType.PROJECT, List.of(), PartType.WEB, 33);
        createAndSaveReview("안드로이드1", CategoryType.SEMINAR, List.of(), PartType.ANDROID, 34);
        createAndSaveReview("iOS1", CategoryType.SEMINAR, List.of(), PartType.IOS, 34);
        createAndSaveReview("기획1", CategoryType.SEMINAR, List.of(), PartType.PLAN, 34);

        // when
        List<ReviewSummaryView> results = queryService.getRandomReviewsByPart();

        // then
        assertThat(results).hasSize(6); // 6개 파트
        assertThat(results)
                .extracting(ReviewSummaryView::partType)
                .containsExactlyInAnyOrder(
                        PartType.SERVER, PartType.DESIGN, PartType.WEB,
                        PartType.ANDROID, PartType.IOS, PartType.PLAN
                );
    }

    @Test
    @DisplayName("특정 파트에 리뷰가 없어도 다른 파트 조회 성공")
    void getRandomReviewsByPart_WithMissingPart_ReturnsAvailable() {
        // given - 서버 파트만 생성
        createAndSaveReview("서버1", CategoryType.SEMINAR, List.of(), PartType.SERVER, 34);
        createAndSaveReview("서버2", CategoryType.PROJECT, List.of(), PartType.SERVER, 33);

        // when
        List<ReviewSummaryView> results = queryService.getRandomReviewsByPart();

        // then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).partType()).isEqualTo(PartType.SERVER);
    }

    @Test
    @DisplayName("리뷰가 하나도 없는 경우 빈 리스트 반환")
    void getRandomReviewsByPart_NoReviews_ReturnsEmptyList() {
        // given - 리뷰 없음

        // when
        List<ReviewSummaryView> results = queryService.getRandomReviewsByPart();

        // then
        assertThat(results).isEmpty();
    }

    // === 작성자별 리뷰 조회 테스트 ===

    @Test
    @DisplayName("작성자명으로 리뷰 목록 조회")
    void getReviewsByAuthor_ReturnsAuthorReviews() {
        // given
        createAndSaveReviewWithAuthor("홍길동 리뷰1", "홍길동", PartType.SERVER);
        createAndSaveReviewWithAuthor("홍길동 리뷰2", "홍길동", PartType.DESIGN);
        createAndSaveReviewWithAuthor("김철수 리뷰", "김철수", PartType.WEB);

        // when
        ReviewsByAuthorView result = queryService.getReviewsByAuthor("홍길동");

        // then
        assertThat(result.reviewCount()).isEqualTo(2);
        assertThat(result.reviews()).hasSize(2);
        assertThat(result.reviews())
                .extracting(ReviewSummaryView::title)
                .containsExactlyInAnyOrder("홍길동 리뷰1", "홍길동 리뷰2");
    }

    @Test
    @DisplayName("작성자가 작성한 리뷰가 없는 경우 빈 리스트 반환")
    void getReviewsByAuthor_NoReviews_ReturnsEmptyList() {
        // given
        createAndSaveReviewWithAuthor("김철수 리뷰", "김철수", PartType.SERVER);

        // when
        ReviewsByAuthorView result = queryService.getReviewsByAuthor("홍길동");

        // then
        assertThat(result.reviewCount()).isEqualTo(0);
        assertThat(result.reviews()).isEmpty();
    }

    @Test
    @DisplayName("View 변환 검증 - 모든 필드 포함")
    void searchReviews_ViewMapping_ContainsAllFields() {
        // given
        Review review = createAndSaveReview(
                "완벽한 리뷰",
                CategoryType.ACTIVITY,
                List.of("세미나", "솝텀"),
                PartType.SERVER,
                34
        );

        ReviewSearchCond cond = new ReviewSearchCond(null, null, null, null);

        // when
        List<ReviewSummaryView> results = queryService.searchReviews(cond, 0, 10);

        // then
        assertThat(results).hasSize(1);
        ReviewSummaryView view = results.get(0);

        assertThat(view.id()).isEqualTo(review.getId());
        assertThat(view.title()).isEqualTo("완벽한 리뷰");
        assertThat(view.description()).isEqualTo("유익한 경험이었습니다.");
        assertThat(view.thumbnailUrl()).isEqualTo("https://example.com/thumb.jpg");
        assertThat(view.platform()).isEqualTo("Medium");
        assertThat(view.url()).isEqualTo(review.getUrlValue());
        assertThat(view.author()).isEqualTo("홍길동");
        assertThat(view.authorProfileImageUrl()).isEqualTo("https://example.com/profile.jpg");
        assertThat(view.generation()).isEqualTo(34);
        assertThat(view.partType()).isEqualTo(PartType.SERVER);
        assertThat(view.category()).isEqualTo("전체 활동");
        assertThat(view.subject()).containsExactly("세미나", "솝텀");
    }

    // === 헬퍼 메서드 ===

    private Review createAndSaveReview(
            String title,
            CategoryType categoryType,
            List<String> subjects,
            PartType partType,
            Integer generation
    ) {
        ReviewContent content = new ReviewContent(
                title,
                "유익한 경험이었습니다.",
                "https://example.com/thumb.jpg",
                "Medium"
        );

        ReviewAuthor author = new ReviewAuthor(
                "홍길동",
                "https://example.com/profile.jpg"
        );

        ReviewCategory category = new ReviewCategory(categoryType);
        ReviewSubjects reviewSubjects = new ReviewSubjects(subjects);

        // URL을 고유하게 만들기 위해 현재 시간 추가
        String uniqueUrl = "https://medium.com/@test/review-" + System.nanoTime();
        ReviewUrl url = new ReviewUrl(uniqueUrl);

        Review review = Review.create(content, author, generation, partType, category, reviewSubjects, url);
        return commandRepository.save(review);
    }

    private Review createAndSaveReviewWithAuthor(String title, String authorName, PartType partType) {
        ReviewContent content = new ReviewContent(
                title,
                "유익한 경험이었습니다.",
                "https://example.com/thumb.jpg",
                "Medium"
        );

        ReviewAuthor author = new ReviewAuthor(
                authorName,
                "https://example.com/profile.jpg"
        );

        ReviewCategory category = new ReviewCategory(CategoryType.SEMINAR);
        ReviewSubjects subjects = new ReviewSubjects(List.of());

        String uniqueUrl = "https://medium.com/@" + authorName + "/review-" + System.nanoTime();
        ReviewUrl url = new ReviewUrl(uniqueUrl);

        Review review = Review.create(content, author, 34, partType, category, subjects, url);
        return commandRepository.save(review);
    }
}