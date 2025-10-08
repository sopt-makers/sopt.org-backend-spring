package sopt.org.homepage.soptstory.service.query.dto;

import sopt.org.homepage.soptstory.domain.SoptStory;

import java.time.LocalDateTime;

/**
 * SoptStory 목록 조회용 View DTO
 *
 * 책임:
 * - 목록 화면에 필요한 정보만 전달
 * - 불변 객체로 안전한 데이터 전달
 */
public record SoptStoryListView(
        Long id,
        String thumbnailUrl,
        String title,
        String description,
        String url,
        LocalDateTime uploadedAt,
        int likeCount,
        boolean isLikedByUser
) {
    /**
     * Entity -> View 변환
     *
     * @param soptStory SoptStory Entity
     * @param isLiked 사용자 좋아요 여부
     * @return SoptStoryListView
     */
    public static SoptStoryListView from(SoptStory soptStory, boolean isLiked) {
        return new SoptStoryListView(
                soptStory.getId(),
                soptStory.getThumbnailUrl(),
                soptStory.getTitle(),
                soptStory.getDescription(),
                soptStory.getUrlValue(),
                soptStory.getCreatedAt(),
                soptStory.getLikeCountValue(),
                isLiked
        );
    }
}