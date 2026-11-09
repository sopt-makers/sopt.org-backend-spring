package sopt.org.homepage.homepagereview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static sopt.org.homepage.application.homepage.review.domain.HomepageReviewType.ACTIVITY;
import static sopt.org.homepage.application.homepage.review.domain.HomepageReviewType.RECRUITING;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.application.admin.dto.request.main.review.AddAdminReviewRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.review.EditAdminReviewRequestDto;
import sopt.org.homepage.application.admin.dto.response.main.review.AddAdminReviewResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.review.EditAdminReviewResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.review.GetAdminReviewResponseRecordDto;
import sopt.org.homepage.application.homepage.review.domain.HomepageReview;
import sopt.org.homepage.application.homepage.review.domain.HomepageReviewType;
import sopt.org.homepage.application.homepage.review.dto.BulkCreateHomepageReviewsCommand;
import sopt.org.homepage.application.homepage.review.dto.BulkCreateHomepageReviewsCommand.ReviewData;
import sopt.org.homepage.application.homepage.review.repository.HomepageReviewRepository;
import sopt.org.homepage.application.homepage.review.service.HomepageReviewService;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.global.exception.ClientBadRequestException;

@DisplayName("HomepageReview 서비스 통합 테스트")
class HomepageReviewServiceTest extends IntegrationTestBase {

    @Autowired
    private HomepageReviewService homepageReviewService;

    @Autowired
    private HomepageReviewRepository homepageReviewRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void tearDown() {
        homepageReviewRepository.deleteAll();
    }

    private void saveReviews(HomepageReviewType type, int count) {
        homepageReviewRepository.saveAll(IntStream.range(0, count)
                .mapToObj(i -> HomepageReview.create(type, "리뷰" + i, "내용" + i, "작성자" + i))
                .toList());
    }

    private static ReviewData reviewData(HomepageReviewType type, String title) {
        return new ReviewData(type, title, "내용", "작성자");
    }

    @Nested
    @DisplayName("홈페이지 리뷰 생성")
    class Create {

        @Test
        @DisplayName("✅ 정상: 리뷰 생성")
        void create_Success() {
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "후회없는활동", "정말 좋은 활동이었습니다", "김솝트 | 36기 | 서버", ACTIVITY
            );

            AddAdminReviewResponseRecordDto result = homepageReviewService.create(request);

            assertThat(result.message()).isEqualTo("홈페이지 리뷰 추가 성공");
            assertThat(homepageReviewRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("✅ 정상: 제목 공백 포함 10자")
        void create_TitleWithSpaces_Success() {
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "후회 없는 활", "내용입니다", "김솝트 | 36기 | 서버", ACTIVITY
            );

            homepageReviewService.create(request);

            assertThat(homepageReviewRepository.findAll().get(0).getTitle()).isEqualTo("후회 없는 활");
        }

        @Test
        @DisplayName("✅ 정상: 지원 후기 생성")
        void create_Recruiting_Success() {
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "서류 합격", "지원 과정이 즐거웠어요", "김솝트 | 37기 | 서버", RECRUITING
            );

            homepageReviewService.create(request);

            assertThat(homepageReviewRepository.findAll().get(0).getType()).isEqualTo(RECRUITING);
        }

        @Test
        @DisplayName("✅ 정상: type 생략 시 활동 후기로 생성")
        void create_WithoutType_DefaultsToActivity() {
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "후회없는활동", "내용입니다", "김솝트 | 36기 | 서버", null
            );

            homepageReviewService.create(request);

            assertThat(homepageReviewRepository.findAll().get(0).getType()).isEqualTo(ACTIVITY);
        }

        @Test
        @DisplayName("✅ 정상: 다른 종류가 가득 차 있어도 생성 가능")
        void create_OtherTypeFull_Success() {
            saveReviews(ACTIVITY, 7);
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "서류 합격", "내용입니다", "작성자", RECRUITING
            );

            homepageReviewService.create(request);

            assertThat(homepageReviewRepository.countByType(RECRUITING)).isEqualTo(1);
        }

        @Test
        @DisplayName("❌ 실패: 같은 종류가 최대 개수(7개)에 도달")
        void create_ExceedsMaxPerType_ThrowsException() {
            saveReviews(ACTIVITY, 7);
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "후회없는활동", "내용입니다", "작성자", ACTIVITY
            );

            assertThatThrownBy(() -> homepageReviewService.create(request))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("최대 7개");
        }

        @Test
        @DisplayName("✅ 정상: type 없는 JSON 요청은 type이 null로 역직렬화된다")
        void deserialize_WithoutType() throws Exception {
            String json = """
                    {"title":"후회없는활동","content":"내용입니다","authorInfo":"작성자"}
                    """;

            AddAdminReviewRequestDto request = objectMapper.readValue(json, AddAdminReviewRequestDto.class);

            assertThat(request.getTitle()).isEqualTo("후회없는활동");
            assertThat(request.getType()).isNull();
        }
    }

    @Nested
    @DisplayName("홈페이지 리뷰 목록 조회")
    class GetList {

        @Test
        @DisplayName("🔍 조회: ID 오름차순 정렬")
        void getReviews_OrderById_Success() {
            homepageReviewRepository.saveAll(List.of(
                    HomepageReview.create(ACTIVITY, "리뷰1", "내용1", "작성자1"),
                    HomepageReview.create(RECRUITING, "리뷰2", "내용2", "작성자2"),
                    HomepageReview.create(ACTIVITY, "리뷰3", "내용3", "작성자3")
            ));

            List<GetAdminReviewResponseRecordDto> result = homepageReviewService.getReviews();

            assertThat(result).hasSize(3);
            assertThat(result).extracting(GetAdminReviewResponseRecordDto::title)
                    .containsExactly("리뷰1", "리뷰2", "리뷰3");
            assertThat(result).extracting(GetAdminReviewResponseRecordDto::type)
                    .containsExactly(ACTIVITY, RECRUITING, ACTIVITY);
        }

        @Test
        @DisplayName("🔍 조회: 빈 목록")
        void getReviews_Empty() {
            List<GetAdminReviewResponseRecordDto> result = homepageReviewService.getReviews();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("🔍 조회: 종류별 7개씩 총 14개도 정상 조회")
        void getReviews_BothTypesFull_Success() {
            saveReviews(ACTIVITY, 7);
            saveReviews(RECRUITING, 7);

            List<GetAdminReviewResponseRecordDto> result = homepageReviewService.getReviews();

            assertThat(result).hasSize(14);
        }
    }

    @Nested
    @DisplayName("홈페이지 리뷰 수정")
    class Edit {

        @Test
        @DisplayName("✅ 정상: 리뷰 수정")
        void edit_Success() {
            HomepageReview saved = homepageReviewRepository.save(
                    HomepageReview.create(ACTIVITY, "원본제목", "원본내용", "원본작성자")
            );
            EditAdminReviewRequestDto request = new EditAdminReviewRequestDto(
                    "수정제목", "수정된 내용입니다", "수정작성자 | 37기", null
            );

            EditAdminReviewResponseRecordDto result = homepageReviewService.edit(saved.getId(), request);

            assertThat(result.message()).isEqualTo("홈페이지 리뷰 수정 성공");
            HomepageReview updated = homepageReviewRepository.findById(saved.getId()).orElseThrow();
            assertThat(updated.getTitle()).isEqualTo("수정제목");
            assertThat(updated.getContent()).isEqualTo("수정된 내용입니다");
            assertThat(updated.getType()).isEqualTo(ACTIVITY);
        }

        @Test
        @DisplayName("✅ 정상: 종류 변경")
        void edit_ChangeType_Success() {
            HomepageReview saved = homepageReviewRepository.save(
                    HomepageReview.create(ACTIVITY, "원본제목", "원본내용", "원본작성자")
            );
            EditAdminReviewRequestDto request = new EditAdminReviewRequestDto(
                    "수정제목", "수정내용", "작성자", RECRUITING
            );

            homepageReviewService.edit(saved.getId(), request);

            assertThat(homepageReviewRepository.findById(saved.getId()).orElseThrow().getType())
                    .isEqualTo(RECRUITING);
        }

        @Test
        @DisplayName("✅ 정상: 같은 종류 유지 시 해당 종류가 가득 차 있어도 수정 가능")
        void edit_SameTypeWhenFull_Success() {
            saveReviews(ACTIVITY, 7);
            HomepageReview target = homepageReviewRepository.findAllByOrderByIdAsc().get(0);
            EditAdminReviewRequestDto request = new EditAdminReviewRequestDto(
                    "수정제목", "수정내용", "작성자", ACTIVITY
            );

            homepageReviewService.edit(target.getId(), request);

            assertThat(homepageReviewRepository.findById(target.getId()).orElseThrow().getTitle())
                    .isEqualTo("수정제목");
        }

        @Test
        @DisplayName("❌ 실패: 가득 찬 종류로 변경")
        void edit_ChangeToFullType_ThrowsException() {
            saveReviews(RECRUITING, 7);
            HomepageReview saved = homepageReviewRepository.save(
                    HomepageReview.create(ACTIVITY, "원본제목", "원본내용", "원본작성자")
            );
            EditAdminReviewRequestDto request = new EditAdminReviewRequestDto(
                    "수정제목", "수정내용", "작성자", RECRUITING
            );

            assertThatThrownBy(() -> homepageReviewService.edit(saved.getId(), request))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("최대 7개");
        }

        @Test
        @DisplayName("❌ 실패: 존재하지 않는 리뷰 수정")
        void edit_NotFound_ThrowsException() {
            EditAdminReviewRequestDto request = new EditAdminReviewRequestDto(
                    "수정제목", "수정내용", "작성자", null
            );

            assertThatThrownBy(() -> homepageReviewService.edit(999L, request))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("not found");
        }
    }

    @Nested
    @DisplayName("홈페이지 리뷰 일괄 교체")
    class BulkCreate {

        @Test
        @DisplayName("✅ 정상: 요청에 포함된 종류만 교체하고 나머지 종류는 유지")
        void bulkCreate_ReplacesOnlyIncludedTypes() {
            saveReviews(ACTIVITY, 3);
            saveReviews(RECRUITING, 2);

            homepageReviewService.bulkCreate(new BulkCreateHomepageReviewsCommand(List.of(
                    reviewData(ACTIVITY, "새활동1"),
                    reviewData(ACTIVITY, "새활동2")
            )));

            List<HomepageReview> all = homepageReviewRepository.findAllByOrderByIdAsc();
            assertThat(all).filteredOn(r -> r.getType() == ACTIVITY)
                    .extracting(HomepageReview::getTitle)
                    .containsExactly("새활동1", "새활동2");
            assertThat(all).filteredOn(r -> r.getType() == RECRUITING).hasSize(2);
        }

        @Test
        @DisplayName("✅ 정상: 두 종류를 함께 보내면 모두 교체")
        void bulkCreate_BothTypes() {
            saveReviews(ACTIVITY, 3);
            saveReviews(RECRUITING, 2);

            homepageReviewService.bulkCreate(new BulkCreateHomepageReviewsCommand(List.of(
                    reviewData(ACTIVITY, "새활동"),
                    reviewData(RECRUITING, "새지원")
            )));

            assertThat(homepageReviewRepository.findAllByOrderByIdAsc())
                    .extracting(HomepageReview::getTitle)
                    .containsExactly("새활동", "새지원");
        }

        @Test
        @DisplayName("✅ 정상: type 없는 항목은 활동 후기로 교체 (지원 후기 유지)")
        void bulkCreate_WithoutType_TreatedAsActivity() {
            saveReviews(ACTIVITY, 3);
            saveReviews(RECRUITING, 2);

            homepageReviewService.bulkCreate(new BulkCreateHomepageReviewsCommand(List.of(
                    reviewData(null, "레거시")
            )));

            assertThat(homepageReviewRepository.countByType(ACTIVITY)).isEqualTo(1);
            assertThat(homepageReviewRepository.countByType(RECRUITING)).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ 정상: 빈 목록이면 전체 삭제")
        void bulkCreate_EmptyList_DeletesAll() {
            saveReviews(ACTIVITY, 3);
            saveReviews(RECRUITING, 2);

            homepageReviewService.bulkCreate(new BulkCreateHomepageReviewsCommand(List.of()));

            assertThat(homepageReviewRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("❌ 실패: 한 종류가 7개를 초과하면 기존 데이터 유지")
        void bulkCreate_ExceedsMaxPerType_ThrowsException() {
            saveReviews(ACTIVITY, 2);
            List<ReviewData> reviews = IntStream.range(0, 8)
                    .mapToObj(i -> reviewData(RECRUITING, "지원" + i))
                    .toList();

            assertThatThrownBy(() -> homepageReviewService.bulkCreate(new BulkCreateHomepageReviewsCommand(reviews)))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("최대 7개");
            assertThat(homepageReviewRepository.countByType(ACTIVITY)).isEqualTo(2);
            assertThat(homepageReviewRepository.countByType(RECRUITING)).isZero();
        }
    }
}
