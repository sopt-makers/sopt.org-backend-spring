package sopt.org.homepage.soptstory.dto;

import java.time.LocalDateTime;
import sopt.org.homepage.soptstory.domain.SoptStory;

/**
 * SoptStory 목록 조회 응답 DTO
 */
public record SoptStoryResponse(
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
     * Entity → Response 직접 변환
     */
    public static SoptStoryResponse from(SoptStory soptStory, boolean isLikedByUser) {
        return new SoptStoryResponse(
                soptStory.getId(),
                soptStory.getThumbnailUrl(),
                soptStory.getTitle(),
                soptStory.getDescription(),
                soptStory.getUrl(),
                soptStory.getCreatedAt(),
                soptStory.getLikeCount(),
                isLikedByUser
        );
    }
}
