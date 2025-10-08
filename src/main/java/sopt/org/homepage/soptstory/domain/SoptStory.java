package sopt.org.homepage.soptstory.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import sopt.org.homepage.soptstory.domain.vo.LikeCount;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryContent;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryUrl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SoptStory 도메인 엔티티
 *
 * 책임:
 * - SoptStory 생성 시 비즈니스 규칙 검증
 * - 좋아요 증가/감소 규칙 관리
 * - 데이터 일관성 유지
 */
@Entity
@Table(name = "\"SoptStory\"")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class SoptStory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"", nullable = false)
    private Long id;

    @Embedded
    private SoptStoryContent content;

    @Embedded
    private SoptStoryUrl url;

    @Embedded
    private LikeCount likeCount;

    @CreationTimestamp
    @Column(name = "\"createdAt\"", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "soptStory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SoptStoryLike> likes = new ArrayList<>();

    /**
     * SoptStory 생성을 위한 팩토리 메서드
     *
     * 비즈니스 규칙:
     * 1. 모든 필드는 필수 (VO 내부에서 검증)
     * 2. URL은 중복될 수 없음 (Repository에서 확인 필요)
     * 3. 초기 좋아요 개수는 0
     *
     * @param content 콘텐츠 (제목, 설명, 썸네일)
     * @param url SoptStory URL
     * @return 생성된 SoptStory 인스턴스
     */
    public static SoptStory create(SoptStoryContent content, SoptStoryUrl url) {
        // 1. 각 VO는 생성 시점에 이미 자가 검증됨

        // 2. 초기 좋아요 개수는 0
        LikeCount initialLikeCount = LikeCount.initial();

        // 3. 엔티티 생성
        return new SoptStory(content, url, initialLikeCount);
    }

    /**
     * Private 생성자 - 팩토리 메서드를 통해서만 생성 가능
     */
    private SoptStory(SoptStoryContent content, SoptStoryUrl url, LikeCount likeCount) {
        this.content = content;
        this.url = url;
        this.likeCount = likeCount;
    }

    /**
     * 좋아요 증가
     *
     * 비즈니스 규칙:
     * - LikeCount VO의 증가 규칙을 따름
     * - 최대값 도달 시 예외 발생
     *
     * @return 변경된 SoptStory (메서드 체이닝용)
     */
    public SoptStory incrementLike() {
        this.likeCount = this.likeCount.increment();
        return this;
    }

    /**
     * 좋아요 감소
     *
     * 비즈니스 규칙:
     * - LikeCount VO의 감소 규칙을 따름
     * - 0 미만으로 내려갈 수 없음
     *
     * @return 변경된 SoptStory (메서드 체이닝용)
     */
    public SoptStory decrementLike() {
        this.likeCount = this.likeCount.decrement();
        return this;
    }

    /**
     * 현재 좋아요 개수 조회
     */
    public int getLikeCountValue() {
        return this.likeCount.getValue();
    }

    /**
     * URL 문자열 조회 (편의 메서드)
     */
    public String getUrlValue() {
        return this.url.getValue();
    }

    /**
     * 제목 조회 (편의 메서드)
     */
    public String getTitle() {
        return this.content.getTitle();
    }

    /**
     * 설명 조회 (편의 메서드)
     */
    public String getDescription() {
        return this.content.getDescription();
    }

    /**
     * 썸네일 URL 조회 (편의 메서드)
     */
    public String getThumbnailUrl() {
        return this.content.getThumbnailUrl();
    }
}