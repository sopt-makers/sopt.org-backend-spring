package sopt.org.homepage.soptstory.controller.dto;

/**
 * SoptStory 생성 응답 DTO
 */
public record CreateSoptStoryResponse(
        String thumbnailUrl,
        String title,
        String description,
        String soptStoryUrl
) {
}