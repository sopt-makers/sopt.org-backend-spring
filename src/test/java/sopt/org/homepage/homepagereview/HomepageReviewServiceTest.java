package sopt.org.homepage.homepagereview;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
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

    @AfterEach
    void tearDown() {
        homepageReviewRepository.deleteAll();
    }

    @Nested
    @DisplayName("홈페이지 리뷰 생성")
    class Create {

        @Test
        @DisplayName("✅ 정상: 리뷰 생성")
        void create_Success() {
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "후회없는활동", "정말 좋은 활동이었습니다", "김솝트 | 36기 | 서버"
            );

            AddAdminReviewResponseRecordDto result = homepageReviewService.create(request);

            assertThat(result.message()).isEqualTo("홈페이지 리뷰 추가 성공");
            assertThat(homepageReviewRepository.findAll()).hasSize(1);
        }

        @Test
        @DisplayName("✅ 정상: 제목 공백 포함 10자")
        void create_TitleWithSpaces_Success() {
            AddAdminReviewRequestDto request = new AddAdminReviewRequestDto(
                    "후회 없는 활", "내용입니다", "김솝트 | 36기 | 서버"
            );

            homepageReviewService.create(request);

            assertThat(homepageReviewRepository.findAll().get(0).getTitle()).isEqualTo("후회 없는 활");
        }
    }

    @Nested
    @DisplayName("홈페이지 리뷰 목록 조회")
    class GetList {

        @Test
        @DisplayName("🔍 조회: ID 오름차순 정렬")
        void getReviews_OrderById_Success() {
            homepageReviewRepository.saveAll(List.of(
                    HomepageReview.create("리뷰1", "내용1", "작성자1"),
                    HomepageReview.create("리뷰2", "내용2", "작성자2"),
                    HomepageReview.create("리뷰3", "내용3", "작성자3")
            ));

            List<GetAdminReviewResponseRecordDto> result = homepageReviewService.getReviews();

            assertThat(result).hasSize(3);
            assertThat(result.get(0).title()).isEqualTo("리뷰1");
            assertThat(result.get(1).title()).isEqualTo("리뷰2");
            assertThat(result.get(2).title()).isEqualTo("리뷰3");
        }

        @Test
        @DisplayName("🔍 조회: 빈 목록")
        void getReviews_Empty() {
            List<GetAdminReviewResponseRecordDto> result = homepageReviewService.getReviews();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("홈페이지 리뷰 수정")
    class Edit {

        @Test
        @DisplayName("✅ 정상: 리뷰 수정")
        void edit_Success() {
            HomepageReview saved = homepageReviewRepository.save(
                    HomepageReview.create("원본제목", "원본내용", "원본작성자")
            );
            EditAdminReviewRequestDto request = new EditAdminReviewRequestDto(
                    "수정제목", "수정된 내용입니다", "수정작성자 | 37기"
            );

            EditAdminReviewResponseRecordDto result = homepageReviewService.edit(saved.getId(), request);

            assertThat(result.message()).isEqualTo("홈페이지 리뷰 수정 성공");
            HomepageReview updated = homepageReviewRepository.findById(saved.getId()).orElseThrow();
            assertThat(updated.getTitle()).isEqualTo("수정제목");
            assertThat(updated.getContent()).isEqualTo("수정된 내용입니다");
        }

        @Test
        @DisplayName("❌ 실패: 존재하지 않는 리뷰 수정")
        void edit_NotFound_ThrowsException() {
            EditAdminReviewRequestDto request = new EditAdminReviewRequestDto(
                    "수정제목", "수정내용", "작성자"
            );

            assertThatThrownBy(() -> homepageReviewService.edit(999L, request))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("not found");
        }
    }
}
