package sopt.org.homepage.soptstory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.exception.AlreadyLikedException;
import sopt.org.homepage.soptstory.exception.DuplicateSoptStoryUrlException;
import sopt.org.homepage.soptstory.exception.NotLikedException;
import sopt.org.homepage.soptstory.exception.SoptStoryNotFoundException;

/**
 * SoptStoryService 통합 테스트
 * <p>
 * 기존 CommandServiceTest + QueryServiceTest를 통합. 모든 비즈니스 시나리오를 실제 DB(TestContainer)로 검증.
 */
@DisplayName("SoptStoryService 통합 테스트")
@Transactional
class SoptStoryServiceTest extends IntegrationTestBase {

    @Autowired
    private SoptStoryService soptStoryService;

    @Autowired
    private SoptStoryRepository soptStoryRepository;

    @Autowired
    private SoptStoryLikeRepository soptStoryLikeRepository;

    @BeforeEach
    void setUp() {
        soptStoryLikeRepository.deleteAll();
        soptStoryRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        soptStoryLikeRepository.deleteAll();
        soptStoryRepository.deleteAll();
    }

    // ===== 생성 테스트 =====

    @Test
    @DisplayName("유효한 값으로 SoptStory 생성 성공")
    void createSoptStory_Success() {
        // when
        Long id = soptStoryService.createSoptStory(
                "SOPT 34기 모집",
                "IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.",
                "https://example.com/thumbnail.jpg",
                "https://blog.sopt.org/2024/recruit"
        );

        // then
        assertThat(id).isNotNull();
        SoptStory saved = soptStoryRepository.findById(id).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("SOPT 34기 모집");
        assertThat(saved.getDescription()).isEqualTo("IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.");
        assertThat(saved.getThumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");
        assertThat(saved.getUrl()).isEqualTo("https://blog.sopt.org/2024/recruit");
        assertThat(saved.getLikeCount()).isZero();
    }

    @Test
    @DisplayName("중복 URL로 생성 시 예외 발생")
    void createSoptStory_DuplicateUrl_ThrowsException() {
        // given
        String url = "https://example.com/article";
        soptStoryService.createSoptStory("제목1", "설명1", null, url);

        // when & then
        assertThatThrownBy(() -> soptStoryService.createSoptStory("제목2", "설명2", null, url))
                .isInstanceOf(DuplicateSoptStoryUrlException.class)
                .hasMessageContaining(url);
    }

    // ===== 좋아요 테스트 =====

    @Test
    @DisplayName("좋아요 성공")
    void like_Success() {
        // given
        Long storyId = createSoptStory();
        String ip = "192.168.0.1";

        // when
        Long likeId = soptStoryService.like(storyId, ip);

        // then
        assertThat(likeId).isNotNull();
        SoptStory updated = soptStoryRepository.findById(storyId).orElseThrow();
        assertThat(updated.getLikeCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 사용자가 같은 SoptStory에 좋아요 성공")
    void like_MultipleUsers_Success() {
        // given
        Long storyId = createSoptStory();

        // when
        soptStoryService.like(storyId, "192.168.0.1");
        soptStoryService.like(storyId, "192.168.0.2");
        soptStoryService.like(storyId, "192.168.0.3");

        // then
        SoptStory updated = soptStoryRepository.findById(storyId).orElseThrow();
        assertThat(updated.getLikeCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 SoptStory에 좋아요 시도하면 예외 발생")
    void like_NotExisting_ThrowsException() {
        assertThatThrownBy(() -> soptStoryService.like(99999L, "192.168.0.1"))
                .isInstanceOf(SoptStoryNotFoundException.class);
    }

    @Test
    @DisplayName("중복 좋아요 시 예외 발생")
    void like_Already_ThrowsException() {
        // given
        Long storyId = createSoptStory();
        String ip = "192.168.0.1";
        soptStoryService.like(storyId, ip);

        // when & then
        assertThatThrownBy(() -> soptStoryService.like(storyId, ip))
                .isInstanceOf(AlreadyLikedException.class);
    }

    // ===== 좋아요 취소 테스트 =====

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlike_Success() {
        // given
        Long storyId = createSoptStory();
        String ip = "192.168.0.1";
        soptStoryService.like(storyId, ip);

        // when
        soptStoryService.unlike(storyId, ip);

        // then
        SoptStory updated = soptStoryRepository.findById(storyId).orElseThrow();
        assertThat(updated.getLikeCount()).isZero();
    }

    @Test
    @DisplayName("좋아요를 누르지 않은 상태에서 취소 시 예외 발생")
    void unlike_NotLiked_ThrowsException() {
        // given
        Long storyId = createSoptStory();

        // when & then
        assertThatThrownBy(() -> soptStoryService.unlike(storyId, "192.168.0.1"))
                .isInstanceOf(NotLikedException.class);
    }

    @Test
    @DisplayName("좋아요 → 취소 → 다시 좋아요 시나리오")
    void like_Unlike_Like_Scenario() {
        // given
        Long storyId = createSoptStory();
        String ip = "192.168.0.1";

        // when & then
        soptStoryService.like(storyId, ip);
        assertThat(soptStoryRepository.findById(storyId).orElseThrow().getLikeCount()).isEqualTo(1);

        soptStoryService.unlike(storyId, ip);
        assertThat(soptStoryRepository.findById(storyId).orElseThrow().getLikeCount()).isZero();

        soptStoryService.like(storyId, ip);
        assertThat(soptStoryRepository.findById(storyId).orElseThrow().getLikeCount()).isEqualTo(1);
    }

    // ===== 목록 조회 테스트 =====

    @Test
    @DisplayName("최신순 정렬 조회")
    void getSoptStoryList_SortByRecent() throws InterruptedException {
        // given
        createSoptStoryWithTitle("제목1", "https://example.com/1");
        Thread.sleep(100);
        createSoptStoryWithTitle("제목2", "https://example.com/2");
        Thread.sleep(100);
        createSoptStoryWithTitle("제목3", "https://example.com/3");

        // when
        Page<SoptStory> result = soptStoryService.getSoptStoryList("recent", 1, 10);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getContent())
                .extracting(SoptStory::getTitle)
                .containsExactly("제목3", "제목2", "제목1");
    }

    @Test
    @DisplayName("좋아요순 정렬 조회")
    void getSoptStoryList_SortByLikes() {
        // given
        Long story1 = createSoptStoryWithTitle("제목1", "https://example.com/1");
        Long story2 = createSoptStoryWithTitle("제목2", "https://example.com/2");
        Long story3 = createSoptStoryWithTitle("제목3", "https://example.com/3");

        soptStoryService.like(story2, "192.168.0.1");
        soptStoryService.like(story2, "192.168.0.2");
        soptStoryService.like(story2, "192.168.0.3");
        soptStoryService.like(story1, "192.168.0.4");

        // when
        Page<SoptStory> result = soptStoryService.getSoptStoryList("likes", 1, 10);

        // then
        assertThat(result.getContent())
                .extracting(SoptStory::getTitle)
                .containsExactly("제목2", "제목1", "제목3");
    }

    @Test
    @DisplayName("페이징 - 첫 번째 페이지 조회")
    void getSoptStoryList_Paging() {
        // given
        for (int i = 1; i <= 5; i++) {
            createSoptStoryWithTitle("제목" + i, "https://example.com/" + i);
        }

        // when
        Page<SoptStory> result = soptStoryService.getSoptStoryList("recent", 1, 3);

        // then
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getTotalPages()).isEqualTo(2);
    }

    @Test
    @DisplayName("빈 목록 조회")
    void getSoptStoryList_Empty() {
        // when
        Page<SoptStory> result = soptStoryService.getSoptStoryList("recent", 1, 10);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    // ===== 좋아요 상태 조회 테스트 =====

    @Test
    @DisplayName("사용자 좋아요 상태 정확히 조회")
    void getLikedSoptStoryIds_CorrectStatus() {
        // given
        Long story1 = createSoptStoryWithTitle("제목1", "https://example.com/1");
        Long story2 = createSoptStoryWithTitle("제목2", "https://example.com/2");
        Long story3 = createSoptStoryWithTitle("제목3", "https://example.com/3");

        String userIp = "192.168.0.100";
        soptStoryService.like(story1, userIp);
        soptStoryService.like(story2, "192.168.0.200");

        Page<SoptStory> page = soptStoryService.getSoptStoryList("recent", 1, 10);

        // when
        Set<Long> likedIds = soptStoryService.getLikedSoptStoryIds(userIp, page.getContent());

        // then
        assertThat(likedIds).containsExactly(story1);
    }

    // ===== Helper =====

    private Long createSoptStory() {
        return soptStoryService.createSoptStory(
                "테스트 제목", "테스트 설명",
                "https://example.com/thumbnail.jpg",
                "https://example.com/test-" + System.currentTimeMillis()
        );
    }

    private Long createSoptStoryWithTitle(String title, String url) {
        return soptStoryService.createSoptStory(title, "테스트 설명", "https://example.com/thumbnail.jpg", url);
    }
}
