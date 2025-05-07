package sopt.org.homepage.soptstory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeSoptStoryResponseDto {
    @Schema(description = "like Id")
    private final Long id;

    @Schema(description = "soptStory Id")
    private final Long soptStoryId;

    @Schema(description = "session Id")
    private final String sessionId;

}