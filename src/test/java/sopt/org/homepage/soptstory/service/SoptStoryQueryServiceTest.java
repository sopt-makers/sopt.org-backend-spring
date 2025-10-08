package sopt.org.homepage.soptstory.service.query;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;
import sopt.org.homepage.soptstory.domain.vo.IpAddress;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryContent;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryUrl;
import sopt.org.homepage.soptstory.repository.command.SoptStoryCommandRepository;
import sopt.org.homepage.soptstory.repository.command.SoptStoryLikeCommandRepository;
import sopt.org.homepage.soptstory.service.query.dto.SoptStoryListView;
import sopt.org.homepage.soptstory.service.query.dto.SoptStoryPageView;
import sopt.org.homepage.soptstory.service.query.dto.SoptStorySearchCond;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * SoptStoryQueryService 통합 테스트
 *
 * 테스트 전략:
 * - 실제 DB 사용 (TestContainer)
 * - @Transactional + @AfterEach로 테스트 격리
 * - Mock 없이 실제 동작 검증
 * - 다양한 정렬 및 페이징 시나리오 검증
 */
@DisplayName("SoptStoryQueryService 통합 테스트")
@Transactional
class SoptStoryQueryServiceTest extends IntegrationTestBase {

    @Autowired
    private SoptStoryQueryService queryService;

    @Autowired
    private SoptStoryCommandRepository soptStoryCommandRepository;

    @Autowired
    private SoptStoryLikeCommandRepository soptStoryLikeCommandRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        soptStoryLikeCommandRepository.deleteAll();
        soptStoryCommandRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        soptStoryLikeCommandRepository.deleteAll();
        soptStoryCommandRepository.deleteAll();
    }

    // ===== 목록 조회 - 정렬 테스트 =====

    @Test
    @DisplayName("최신순 정렬 - 생성 시간 역순으로 조회")
    void getSoptStoryList_SortByRecent_Success() throws InterruptedException {
        // given
        SoptStory story1 = createAndSaveSoptStory("제목1", "https://example.com/1");
        Thread.sleep(100);  // 생성 시간 차이를 만들기 위해
        SoptStory story2 = createAndSaveSoptStory("제목2", "https://example.com/2");
        Thread.sleep(100);
        SoptStory story3 = createAndSaveSoptStory("제목3", "https://example.com/3");

        SoptStorySearchCond cond = new SoptStorySearchCond("recent", 1, 10);

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, "192.168.0.1");

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.content())
                .extracting(SoptStoryListView::title)
                .containsExactly("제목3", "제목2", "제목1");  // 최신순
    }

    @Test
    @DisplayName("좋아요순 정렬 - 좋아요 개수 많은 순으로 조회")
    void getSoptStoryList_SortByLikes_Success() {
        // given
        SoptStory story1 = createAndSaveSoptStory("제목1", "https://example.com/1");
        SoptStory story2 = createAndSaveSoptStory("제목2", "https://example.com/2");
        SoptStory story3 = createAndSaveSoptStory("제목3", "https://example.com/3");

        // story2에 좋아요 3개
        addLikes(story2, "192.168.0.1", "192.168.0.2", "192.168.0.3");
        // story1에 좋아요 1개
        addLikes(story1, "192.168.0.4");

        SoptStorySearchCond cond = new SoptStorySearchCond("likes", 1, 10);

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, "192.168.0.100");

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.content())
                .extracting(SoptStoryListView::title)
                .containsExactly("제목2", "제목1", "제목3");  // 좋아요 개수 순
        assertThat(result.content().get(0).likeCount()).isEqualTo(3);
        assertThat(result.content().get(1).likeCount()).isEqualTo(1);
        assertThat(result.content().get(2).likeCount()).isZero();
    }

    // ===== 목록 조회 - 페이징 테스트 =====

    @Test
    @DisplayName("페이징 - 첫 번째 페이지 조회")
    void getSoptStoryList_FirstPage_Success() {
        // given
        createMultipleSoptStories(5);

        SoptStorySearchCond cond = new SoptStorySearchCond("recent", 1, 3);

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, "192.168.0.1");

        // then
        assertThat(result.content()).hasSize(3);
        assertThat(result.totalCount()).isEqualTo(5);
        assertThat(result.currentPage()).isEqualTo(1);
        assertThat(result.pageSize()).isEqualTo(3);
        assertThat(result.totalPages()).isEqualTo(2);  // 5 / 3 = 2페이지
        assertThat(result.hasNext()).isTrue();
        assertThat(result.hasPrevious()).isFalse();
    }

    @Test
    @DisplayName("페이징 - 두 번째 페이지 조회")
    void getSoptStoryList_SecondPage_Success() {
        // given
        createMultipleSoptStories(5);

        SoptStorySearchCond cond = new SoptStorySearchCond("recent", 2, 3);

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, "192.168.0.1");

        // then
        assertThat(result.content()).hasSize(2);  // 나머지 2개
        assertThat(result.currentPage()).isEqualTo(2);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.hasPrevious()).isTrue();
    }

    @Test
    @DisplayName("페이징 - 빈 페이지 조회")
    void getSoptStoryList_EmptyPage_Success() {
        // given
        createMultipleSoptStories(3);

        SoptStorySearchCond cond = new SoptStorySearchCond("recent", 5, 10);  // 존재하지 않는 페이지

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, "192.168.0.1");

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalCount()).isEqualTo(3);
    }

    // ===== 좋아요 상태 조회 테스트 =====

    @Test
    @DisplayName("사용자가 좋아요를 누른 SoptStory는 isLikedByUser가 true")
    void getSoptStoryList_WithLikedStories_ReturnsCorrectLikeStatus() {
        // given
        SoptStory story1 = createAndSaveSoptStory("제목1", "https://example.com/1");
        SoptStory story2 = createAndSaveSoptStory("제목2", "https://example.com/2");
        SoptStory story3 = createAndSaveSoptStory("제목3", "https://example.com/3");

        String userIp = "192.168.0.100";
        addLikes(story1, userIp);  // 사용자가 story1에만 좋아요
        addLikes(story2, "192.168.0.200");  // 다른 사용자가 story2에 좋아요

        SoptStorySearchCond cond = new SoptStorySearchCond("recent", 1, 10);

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, userIp);

        // then
        List<SoptStoryListView> content = result.content();
        SoptStoryListView story1View = content.stream()
                .filter(v -> v.title().equals("제목1"))
                .findFirst()
                .orElseThrow();
        SoptStoryListView story2View = content.stream()
                .filter(v -> v.title().equals("제목2"))
                .findFirst()
                .orElseThrow();
        SoptStoryListView story3View = content.stream()
                .filter(v -> v.title().equals("제목3"))
                .findFirst()
                .orElseThrow();

        assertThat(story1View.isLikedByUser()).isTrue();   // 사용자가 좋아요 누름
        assertThat(story2View.isLikedByUser()).isFalse();  // 다른 사용자가 좋아요 누름
        assertThat(story3View.isLikedByUser()).isFalse();  // 아무도 좋아요 안 누름
    }

    @Test
    @DisplayName("좋아요를 누르지 않은 사용자는 모든 isLikedByUser가 false")
    void getSoptStoryList_NoLikes_AllFalse() {
        // given
        createAndSaveSoptStory("제목1", "https://example.com/1");
        createAndSaveSoptStory("제목2", "https://example.com/2");

        String userIp = "192.168.0.100";
        SoptStorySearchCond cond = new SoptStorySearchCond("recent", 1, 10);

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, userIp);

        // then
        assertThat(result.content())
                .extracting(SoptStoryListView::isLikedByUser)
                .containsOnly(false);
    }

    // ===== 빈 결과 테스트 =====

    @Test
    @DisplayName("SoptStory가 없으면 빈 목록 반환")
    void getSoptStoryList_NoData_ReturnsEmptyList() {
        // given
        SoptStorySearchCond cond = new SoptStorySearchCond("recent", 1, 10);

        // when
        SoptStoryPageView result = queryService.getSoptStoryList(cond, "192.168.0.1");

        // then
        assertThat(result.content()).isEmpty();
        assertThat(result.totalCount()).isZero();
        assertThat(result.totalPages()).isZero();
    }

    // ===== Helper Methods =====

    private SoptStory createAndSaveSoptStory(String title, String url) {
        SoptStoryContent content = new SoptStoryContent(
                title,
                "테스트 설명",
                "https://example.com/thumbnail.jpg"
        );
        SoptStoryUrl soptStoryUrl = new SoptStoryUrl(url);
        SoptStory soptStory = SoptStory.create(content, soptStoryUrl);
        return soptStoryCommandRepository.save(soptStory);
    }

    private void createMultipleSoptStories(int count) {
        for (int i = 1; i <= count; i++) {
            createAndSaveSoptStory("제목" + i, "https://example.com/" + i);
        }
    }

    private void addLikes(SoptStory soptStory, String... ips) {
        for (String ip : ips) {
            IpAddress ipAddress = new IpAddress(ip);
            SoptStoryLike like = SoptStoryLike.create(soptStory, ipAddress);
            soptStoryLikeCommandRepository.save(like);
            soptStory.incrementLike();
        }
        soptStoryCommandRepository.save(soptStory);
    }
}