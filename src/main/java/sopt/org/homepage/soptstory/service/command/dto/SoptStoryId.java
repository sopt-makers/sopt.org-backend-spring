package sopt.org.homepage.soptstory.service.command.dto;

/**
 * SoptStory ID를 담는 DTO
 *
 * @param value SoptStory ID
 */
public record SoptStoryId(Long value) {
    public SoptStoryId {
        if (value == null) {
            throw new IllegalArgumentException("SoptStory ID는 필수입니다.");
        }
    }
}