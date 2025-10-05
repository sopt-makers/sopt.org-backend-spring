package sopt.org.homepage.soptstory.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.soptstory.domain.vo.IpAddress;

/**
 * SoptStory 좋아요 도메인 엔티티
 *
 * 책임:
 * - 특정 IP의 SoptStory 좋아요 기록 관리
 * - IP 주소 유효성 검증
 */
@Entity
@Table(
        name = "\"SoptStoryLike\"",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_soptstory_like_ip",
                        columnNames = {"\"soptStoryId\"", "\"ip\""}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class SoptStoryLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"soptStoryId\"", nullable = false)
    private SoptStory soptStory;

    @Embedded
    private IpAddress ipAddress;

    /**
     * 좋아요 생성을 위한 팩토리 메서드
     *
     * 비즈니스 규칙:
     * 1. SoptStory와 IP 주소는 필수
     * 2. 동일 IP의 중복 좋아요는 Repository에서 검증 필요
     *
     * @param soptStory 좋아요 대상 SoptStory
     * @param ipAddress 좋아요를 누른 사용자의 IP 주소
     * @return 생성된 SoptStoryLike 인스턴스
     */
    public static SoptStoryLike create(SoptStory soptStory, IpAddress ipAddress) {
        validateSoptStory(soptStory);
        // IpAddress는 VO 생성 시점에 이미 검증됨

        return new SoptStoryLike(soptStory, ipAddress);
    }

    /**
     * Private 생성자 - 팩토리 메서드를 통해서만 생성 가능
     */
    private SoptStoryLike(SoptStory soptStory, IpAddress ipAddress) {
        this.soptStory = soptStory;
        this.ipAddress = ipAddress;
    }

    private static void validateSoptStory(SoptStory soptStory) {
        if (soptStory == null) {
            throw new IllegalArgumentException("SoptStory는 필수입니다.");
        }
    }

    /**
     * IP 주소 문자열 조회 (편의 메서드)
     */
    public String getIpAddressValue() {
        return this.ipAddress.getValue();
    }

    /**
     * SoptStory ID 조회 (편의 메서드)
     */
    public Long getSoptStoryId() {
        return this.soptStory.getId();
    }
}