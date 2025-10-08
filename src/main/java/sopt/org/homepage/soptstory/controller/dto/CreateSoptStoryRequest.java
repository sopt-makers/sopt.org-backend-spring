
package sopt.org.homepage.soptstory.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * SoptStory 생성 요청 DTO
 */
public record CreateSoptStoryRequest(
        @NotBlank(message = "링크는 필수입니다.")
        @Size(max = 500, message = "링크는 500자를 초과할 수 없습니다.")
        String link
) {
}