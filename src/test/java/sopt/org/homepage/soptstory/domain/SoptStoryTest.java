package sopt.org.homepage.soptstory.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sopt.org.homepage.soptstory.domain.vo.IpAddress;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryContent;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryUrl;

import static org.assertj.core.api.Assertions.*;

/**
 * SoptStoryLike Entity 단위 테스트
 */
@DisplayName("SoptStoryLike Entity 단위 테스트")
class SoptStoryLikeTest {

    @Test
    @DisplayName("유효한 값으로 SoptStoryLike 생성 성공")
    void createSoptStoryLike() {
        // given
        SoptStory soptStory = createDefaultSoptStory();
        IpAddress ipAddress = new IpAddress("192.168.0.1");

        // when
        SoptStoryLike like = SoptStoryLike.create(soptStory, ipAddress);

        // then
        assertThat(like).isNotNull();
        assertThat(like.getSoptStory()).isEqualTo(soptStory);
        assertThat(like.getIpAddress()).isEqualTo(ipAddress);
    }

    @Test
    @DisplayName("SoptStory가 null이면 예외 발생")
    void createWithNullSoptStory() {
        // given
        IpAddress ipAddress = new IpAddress("192.168.0.1");

        // when & then
        assertThatThrownBy(() -> SoptStoryLike.create(null, ipAddress))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("SoptStory는 필수입니다.");
    }

    @Test
    @DisplayName("IpAddress가 null이면 예외 발생")
    void createWithNullIpAddress() {
        // given
        SoptStory soptStory = createDefaultSoptStory();

        // when & then
        assertThatThrownBy(() -> SoptStoryLike.create(soptStory, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("getIpAddressValue()는 IP 주소 문자열을 반환한다")
    void getIpAddressValue() {
        // given
        SoptStory soptStory = createDefaultSoptStory();
        String ip = "192.168.0.1";
        IpAddress ipAddress = new IpAddress(ip);
        SoptStoryLike like = SoptStoryLike.create(soptStory, ipAddress);

        // when & then
        assertThat(like.getIpAddressValue()).isEqualTo(ip);
    }

    @Test
    @DisplayName("getSoptStoryId()는 연관된 SoptStory의 ID를 반환한다")
    void getSoptStoryId() {
        // given
        SoptStory soptStory = createSoptStoryWithId(100L);
        IpAddress ipAddress = new IpAddress("192.168.0.1");
        SoptStoryLike like = SoptStoryLike.create(soptStory, ipAddress);

        // when & then
        assertThat(like.getSoptStoryId()).isEqualTo(100L);
    }

    @Test
    @DisplayName("동일한 SoptStory와 IP로 여러 좋아요 생성 가능 (Entity 레벨)")
    void createMultipleLikesWithSameData() {
        // given
        SoptStory soptStory = createDefaultSoptStory();
        IpAddress ipAddress = new IpAddress("192.168.0.1");

        // when
        SoptStoryLike like1 = SoptStoryLike.create(soptStory, ipAddress);
        SoptStoryLike like2 = SoptStoryLike.create(soptStory, ipAddress);

        // then: Entity 레벨에서는 생성 가능 (중복 방지는 Repository/Service 책임)
        assertThat(like1).isNotNull();
        assertThat(like2).isNotNull();
        assertThat(like1).isNotSameAs(like2);
    }

    @Test
    @DisplayName("다른 IP로 같은 SoptStory에 좋아요 생성 가능")
    void createMultipleLikesWithDifferentIps() {
        // given
        SoptStory soptStory = createDefaultSoptStory();
        IpAddress ip1 = new IpAddress("192.168.0.1");
        IpAddress ip2 = new IpAddress("192.168.0.2");

        // when
        SoptStoryLike like1 = SoptStoryLike.create(soptStory, ip1);
        SoptStoryLike like2 = SoptStoryLike.create(soptStory, ip2);

        // then
        assertThat(like1.getIpAddressValue()).isEqualTo("192.168.0.1");
        assertThat(like2.getIpAddressValue()).isEqualTo("192.168.0.2");
    }

    @Test
    @DisplayName("IPv6 주소로도 좋아요 생성 가능")
    void createWithIpv6() {
        // given
        SoptStory soptStory = createDefaultSoptStory();
        IpAddress ipAddress = new IpAddress("2001:0db8:85a3:0000:0000:8a2e:0370:7334");

        // when
        SoptStoryLike like = SoptStoryLike.create(soptStory, ipAddress);

        // then
        assertThat(like.getIpAddressValue())
                .isEqualTo("2001:0db8:85a3:0000:0000:8a2e:0370:7334");
    }

    // ===== Helper Methods =====

    private SoptStory createDefaultSoptStory() {
        SoptStoryContent content = new SoptStoryContent(
                "테스트 제목",
                "테스트 설명",
                "https://example.com/thumbnail.jpg"
        );
        SoptStoryUrl url = new SoptStoryUrl("https://example.com/test-article");
        return SoptStory.create(content, url);
    }

    /**
     * ID가 설정된 SoptStory 생성 (테스트용)
     * 실제로는 JPA가 ID를 설정하지만, 단위 테스트에서는 Reflection 사용
     */
    private SoptStory createSoptStoryWithId(Long id) {
        SoptStory soptStory = createDefaultSoptStory();

        try {
            java.lang.reflect.Field idField = SoptStory.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(soptStory, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set id for test", e);
        }

        return soptStory;
    }
}