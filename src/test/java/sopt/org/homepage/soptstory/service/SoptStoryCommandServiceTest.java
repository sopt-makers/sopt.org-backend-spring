package sopt.org.homepage.soptstory.service.command;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;
import sopt.org.homepage.soptstory.exception.AlreadyLikedException;
import sopt.org.homepage.soptstory.exception.DuplicateSoptStoryUrlException;
import sopt.org.homepage.soptstory.exception.NotLikedException;
import sopt.org.homepage.soptstory.exception.SoptStoryNotFoundException;
import sopt.org.homepage.soptstory.repository.command.SoptStoryCommandRepository;
import sopt.org.homepage.soptstory.repository.command.SoptStoryLikeCommandRepository;
import sopt.org.homepage.soptstory.service.command.dto.*;

import static org.assertj.core.api.Assertions.*;

/**
 * SoptStoryCommandService 통합 테스트
 *
 * 테스트 전략:
 * - 실제 DB 사용 (TestContainer)
 * - @Transactional + @AfterEach로 테스트 격리
 * - Mock 없이 실제 동작 검증
 * - 비즈니스 규칙이 도메인에서 작동하는지 확인
 */
@DisplayName("SoptStoryCommandService 통합 테스트")
@Transactional
class SoptStoryCommandServiceTest extends IntegrationTestBase {

    @Autowired
    private SoptStoryCommandService commandService;

    @Autowired
    private SoptStoryCommandRepository soptStoryCommandRepository;

    @Autowired
    private SoptStoryLikeCommandRepository soptStoryLikeCommandRepository;

    @AfterEach
    void tearDown() {
        soptStoryLikeCommandRepository.deleteAll();
        soptStoryCommandRepository.deleteAll();
    }

    // ===== SoptStory 생성 테스트 =====

    @Test
    @DisplayName("유효한 Command로 SoptStory 생성 성공")
    void createSoptStory_WithValidCommand_Success() {
        // given
        CreateSoptStoryCommand command = new CreateSoptStoryCommand(
                "SOPT 34기 모집",
                "IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.",
                "https://example.com/thumbnail.jpg",
                "https://blog.sopt.org/2024/recruit"
        );

        // when
        SoptStoryId result = commandService.createSoptStory(command);

        // then
        assertThat(result.value()).isNotNull();

        // DB 검증
        SoptStory saved = soptStoryCommandRepository.findById(result.value()).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("SOPT 34기 모집");
        assertThat(saved.getDescription()).isEqualTo("IT 벤처 창업 동아리 SOPT가 34기를 모집합니다.");
        assertThat(saved.getThumbnailUrl()).isEqualTo("https://example.com/thumbnail.jpg");
        assertThat(saved.getUrlValue()).isEqualTo("https://blog.sopt.org/2024/recruit");
        assertThat(saved.getLikeCountValue()).isZero();
    }

    @Test
    @DisplayName("썸네일 없이 SoptStory 생성 성공")
    void createSoptStory_WithoutThumbnail_Success() {
        // given
        CreateSoptStoryCommand command = new CreateSoptStoryCommand(
                "제목",
                "설명",
                null,  // 썸네일 없음
                "https://example.com/article"
        );

        // when
        SoptStoryId result = commandService.createSoptStory(command);

        // then
        SoptStory saved = soptStoryCommandRepository.findById(result.value()).orElseThrow();
        assertThat(saved.getThumbnailUrl()).isNull();
    }

    @Test
    @DisplayName("중복 URL로 생성 시도하면 예외 발생")
    void createSoptStory_WithDuplicateUrl_ThrowsException() {
        // given
        String duplicateUrl = "https://example.com/same-article";
        CreateSoptStoryCommand command1 = new CreateSoptStoryCommand(
                "제목1", "설명1", null, duplicateUrl
        );
        commandService.createSoptStory(command1);

        CreateSoptStoryCommand command2 = new CreateSoptStoryCommand(
                "제목2", "설명2", null, duplicateUrl
        );

        // when & then
        assertThatThrownBy(() -> commandService.createSoptStory(command2))
                .isInstanceOf(DuplicateSoptStoryUrlException.class)
                .hasMessageContaining(duplicateUrl);
    }

    // ===== 좋아요 추가 테스트 =====

    @Test
    @DisplayName("좋아요 추가 성공")
    void like_Success() {
        // given
        SoptStory soptStory = createAndSaveSoptStory();
        String userIp = "192.168.0.1";

        LikeSoptStoryCommand command = new LikeSoptStoryCommand(
                soptStory.getId(),
                userIp
        );

        // when
        SoptStoryLikeId result = commandService.like(command);

        // then
        assertThat(result.value()).isNotNull();

        // DB 검증 - SoptStoryLike 생성됨
        SoptStoryLike savedLike = soptStoryLikeCommandRepository.findById(result.value()).orElseThrow();
        assertThat(savedLike.getSoptStoryId()).isEqualTo(soptStory.getId());
        assertThat(savedLike.getIpAddressValue()).isEqualTo(userIp);

        // DB 검증 - SoptStory 좋아요 개수 증가
        SoptStory updatedStory = soptStoryCommandRepository.findById(soptStory.getId()).orElseThrow();
        assertThat(updatedStory.getLikeCountValue()).isEqualTo(1);
    }

    @Test
    @DisplayName("여러 사용자가 같은 SoptStory에 좋아요 성공")
    void like_MultipleUsers_Success() {
        // given
        SoptStory soptStory = createAndSaveSoptStory();
        String ip1 = "192.168.0.1";
        String ip2 = "192.168.0.2";
        String ip3 = "192.168.0.3";

        // when
        commandService.like(new LikeSoptStoryCommand(soptStory.getId(), ip1));
        commandService.like(new LikeSoptStoryCommand(soptStory.getId(), ip2));
        commandService.like(new LikeSoptStoryCommand(soptStory.getId(), ip3));

        // then
        SoptStory updated = soptStoryCommandRepository.findById(soptStory.getId()).orElseThrow();
        assertThat(updated.getLikeCountValue()).isEqualTo(3);
    }

    @Test
    @DisplayName("존재하지 않는 SoptStory에 좋아요 시도하면 예외 발생")
    void like_NotExistingSoptStory_ThrowsException() {
        // given
        Long notExistingId = 99999L;
        LikeSoptStoryCommand command = new LikeSoptStoryCommand(notExistingId, "192.168.0.1");

        // when & then
        assertThatThrownBy(() -> commandService.like(command))
                .isInstanceOf(SoptStoryNotFoundException.class)
                .hasMessageContaining(notExistingId.toString());
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태에서 다시 좋아요 시도하면 예외 발생")
    void like_AlreadyLiked_ThrowsException() {
        // given
        SoptStory soptStory = createAndSaveSoptStory();
        String userIp = "192.168.0.1";

        LikeSoptStoryCommand command = new LikeSoptStoryCommand(soptStory.getId(), userIp);
        commandService.like(command);

        // when & then
        assertThatThrownBy(() -> commandService.like(command))
                .isInstanceOf(AlreadyLikedException.class)
                .hasMessageContaining(soptStory.getId().toString())
                .hasMessageContaining(userIp);
    }

    // ===== 좋아요 취소 테스트 =====

    @Test
    @DisplayName("좋아요 취소 성공")
    void unlike_Success() {
        // given
        SoptStory soptStory = createAndSaveSoptStory();
        String userIp = "192.168.0.1";

        LikeSoptStoryCommand likeCommand = new LikeSoptStoryCommand(soptStory.getId(), userIp);
        SoptStoryLikeId likeId = commandService.like(likeCommand);

        UnlikeSoptStoryCommand unlikeCommand = new UnlikeSoptStoryCommand(soptStory.getId(), userIp);

        // when
        SoptStoryLikeId result = commandService.unlike(unlikeCommand);

        // then
        assertThat(result.value()).isEqualTo(likeId.value());

        // DB 검증 - SoptStoryLike 삭제됨
        assertThat(soptStoryLikeCommandRepository.findById(likeId.value())).isEmpty();

        // DB 검증 - SoptStory 좋아요 개수 감소
        SoptStory updated = soptStoryCommandRepository.findById(soptStory.getId()).orElseThrow();
        assertThat(updated.getLikeCountValue()).isZero();
    }

    @Test
    @DisplayName("좋아요 여러 개 있을 때 하나만 취소")
    void unlike_OneOfMultiple_Success() {
        // given
        SoptStory soptStory = createAndSaveSoptStory();
        String ip1 = "192.168.0.1";
        String ip2 = "192.168.0.2";

        commandService.like(new LikeSoptStoryCommand(soptStory.getId(), ip1));
        commandService.like(new LikeSoptStoryCommand(soptStory.getId(), ip2));

        // when
        commandService.unlike(new UnlikeSoptStoryCommand(soptStory.getId(), ip1));

        // then
        SoptStory updated = soptStoryCommandRepository.findById(soptStory.getId()).orElseThrow();
        assertThat(updated.getLikeCountValue()).isEqualTo(1);

        // ip2의 좋아요는 여전히 존재
        boolean ip2LikeExists = soptStoryLikeCommandRepository
                .existsBySoptStory_IdAndIpAddress_Value(soptStory.getId(), ip2);
        assertThat(ip2LikeExists).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 SoptStory에 좋아요 취소 시도하면 예외 발생")
    void unlike_NotExistingSoptStory_ThrowsException() {
        // given
        Long notExistingId = 99999L;
        UnlikeSoptStoryCommand command = new UnlikeSoptStoryCommand(notExistingId, "192.168.0.1");

        // when & then
        assertThatThrownBy(() -> commandService.unlike(command))
                .isInstanceOf(SoptStoryNotFoundException.class)
                .hasMessageContaining(notExistingId.toString());
    }

    @Test
    @DisplayName("좋아요를 누르지 않은 상태에서 취소 시도하면 예외 발생")
    void unlike_NotLiked_ThrowsException() {
        // given
        SoptStory soptStory = createAndSaveSoptStory();
        String userIp = "192.168.0.1";

        UnlikeSoptStoryCommand command = new UnlikeSoptStoryCommand(soptStory.getId(), userIp);

        // when & then
        assertThatThrownBy(() -> commandService.unlike(command))
                .isInstanceOf(NotLikedException.class)
                .hasMessageContaining(soptStory.getId().toString())
                .hasMessageContaining(userIp);
    }

    @Test
    @DisplayName("좋아요 후 취소 후 다시 좋아요 시나리오")
    void like_Unlike_Like_Scenario() {
        // given
        SoptStory soptStory = createAndSaveSoptStory();
        String userIp = "192.168.0.1";

        LikeSoptStoryCommand likeCommand = new LikeSoptStoryCommand(soptStory.getId(), userIp);
        UnlikeSoptStoryCommand unlikeCommand = new UnlikeSoptStoryCommand(soptStory.getId(), userIp);

        // when
        commandService.like(likeCommand);
        SoptStory afterLike = soptStoryCommandRepository.findById(soptStory.getId()).orElseThrow();
        assertThat(afterLike.getLikeCountValue()).isEqualTo(1);

        commandService.unlike(unlikeCommand);
        SoptStory afterUnlike = soptStoryCommandRepository.findById(soptStory.getId()).orElseThrow();
        assertThat(afterUnlike.getLikeCountValue()).isZero();

        commandService.like(likeCommand);
        SoptStory afterReLike = soptStoryCommandRepository.findById(soptStory.getId()).orElseThrow();
        assertThat(afterReLike.getLikeCountValue()).isEqualTo(1);
    }

    // ===== Helper Methods =====

    private SoptStory createAndSaveSoptStory() {
        CreateSoptStoryCommand command = new CreateSoptStoryCommand(
                "테스트 제목",
                "테스트 설명",
                "https://example.com/thumbnail.jpg",
                "https://example.com/test-article-" + System.currentTimeMillis()
        );
        SoptStoryId id = commandService.createSoptStory(command);
        return soptStoryCommandRepository.findById(id.value()).orElseThrow();
    }
}