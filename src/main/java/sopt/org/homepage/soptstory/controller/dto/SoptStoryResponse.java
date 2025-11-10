package sopt.org.homepage.soptstory.controller.dto;

import sopt.org.homepage.soptstory.service.query.dto.SoptStoryListView;

import java.time.LocalDateTime;

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
     * View DTO -> Response DTO 변환
     */
    public static SoptStoryResponse from(SoptStoryListView view) {
        return new SoptStoryResponse(
                view.id(),
                view.thumbnailUrl(),
                view.title(),
                view.description(),
                view.url(),
                view.uploadedAt(),
                view.likeCount(),
                view.isLikedByUser()
        );
    }
}