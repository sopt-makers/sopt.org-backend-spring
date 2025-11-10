
package sopt.org.homepage.soptstory.service.command.dto;

/**
 * SoptStoryLike ID를 담는 DTO
 *
 * @param value SoptStoryLike ID
 */
public record SoptStoryLikeId(Long value) {
    public SoptStoryLikeId {
        if (value == null) {
            throw new IllegalArgumentException("SoptStoryLike ID는 필수입니다.");
        }
    }
}