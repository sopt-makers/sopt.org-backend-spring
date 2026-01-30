package sopt.org.homepage.news;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.infrastructure.aws.s3.S3Service;
import sopt.org.homepage.news.dto.AddAdminNewsV2RequestDto;
import sopt.org.homepage.news.dto.DeleteAdminNewsRequestDto;
import sopt.org.homepage.news.dto.GetAdminNewsRequestDto;
import sopt.org.homepage.news.dto.GetAdminNewsResponseDto;

/**
 * News 통합 테스트
 * <p>
 * 인수인계 목적: - News는 최신소식을 나타냄 - S3에 이미지 업로드/삭제 연동 - Admin에서 CRUD 관리
 */
@DisplayName("News 서비스 통합 테스트")
class NewsServiceTest extends IntegrationTestBase {

    @Autowired
    private NewsService newsService;

    @Autowired
    private NewsRepository newsRepository;

    @MockBean
    private S3Service s3Service;

    @AfterEach
    void tearDown() {
        newsRepository.deleteAll();
    }

    // ===== 생성 시나리오 =====

    @Nested
    @DisplayName("최신소식 생성")
    class Create {

        @Test
        @DisplayName("✅ 정상: Presigned URL 방식 생성")
        void addMainNewsV2_Success() {
            // given
            AddAdminNewsV2RequestDto request = AddAdminNewsV2RequestDto.builder()
                    .title("SOPT 35기 모집")
                    .link("https://sopt.org/recruit")
                    .imageUrl("https://s3.amazonaws.com/bucket/image.jpg")
                    .build();

            // when
            newsService.addMainNewsV2(request);

            // then
            List<News> all = newsRepository.findAll();
            assertThat(all).hasSize(1);
            assertThat(all.get(0).getTitle()).isEqualTo("SOPT 35기 모집");
            assertThat(all.get(0).getImage()).isEqualTo("https://s3.amazonaws.com/bucket/image.jpg");
        }
    }

    // ===== 삭제 시나리오 =====

    @Nested
    @DisplayName("최신소식 삭제")
    class Delete {

        @Test
        @DisplayName("✅ 정상: 삭제")
        void deleteMainNews_Success() {
            // given
            News saved = newsRepository.save(createEntity("테스트 뉴스"));
            doNothing().when(s3Service).deleteFile(anyString());

            DeleteAdminNewsRequestDto request = new DeleteAdminNewsRequestDto(saved.getId());

            // when
            newsService.deleteMainNews(request);

            // then
            assertThat(newsRepository.findAll()).isEmpty();
        }

        @Test
        @DisplayName("❌ 실패: 존재하지 않는 뉴스 삭제")
        void deleteMainNews_NotFound_ThrowsException() {
            // given
            DeleteAdminNewsRequestDto request = new DeleteAdminNewsRequestDto(999);

            // when & then
            assertThatThrownBy(() -> newsService.deleteMainNews(request))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("not found");
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("최신소식 조회")
    class Find {

        @Test
        @DisplayName("🔍 조회: 단건 조회")
        void getMainNews_Success() {
            // given
            News saved = newsRepository.save(createEntity("테스트 뉴스"));

            GetAdminNewsRequestDto request = new GetAdminNewsRequestDto(saved.getId());

            // when
            GetAdminNewsResponseDto result = newsService.getMainNews(request);

            // then
            assertThat(result.getId()).isEqualTo(saved.getId());
            assertThat(result.getTitle()).isEqualTo("테스트 뉴스");
        }

        @Test
        @DisplayName("🔍 조회: 전체 조회")
        void findAll_Success() {
            // given
            newsRepository.saveAll(List.of(
                    createEntity("뉴스 1"),
                    createEntity("뉴스 2"),
                    createEntity("뉴스 3")
            ));

            // when
            List<News> result = newsService.findAll();

            // then
            assertThat(result).hasSize(3);
        }


        @Test
        @DisplayName("❌ 조회: 존재하지 않는 뉴스")
        void getMainNews_NotFound_ThrowsException() {
            // given
            GetAdminNewsRequestDto request = new GetAdminNewsRequestDto(999);

            // when & then
            assertThatThrownBy(() -> newsService.getMainNews(request))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("not found");
        }
    }

    // ===== Helper Methods =====

    private News createEntity(String title) {
        return News.builder()
                .title(title)
                .link("https://example.com/" + title)
                .image("https://s3.amazonaws.com/bucket/" + title + ".jpg")
                .build();
    }
}
