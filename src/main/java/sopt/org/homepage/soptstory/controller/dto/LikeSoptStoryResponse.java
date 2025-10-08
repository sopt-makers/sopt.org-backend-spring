package sopt.org.homepage.soptstory.controller.dto;

/**
 * 좋아요 응답 DTO
 */
public record LikeSoptStoryResponse(
        Long id,
        Long soptStoryId,
        String ip
) {
}