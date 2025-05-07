package sopt.org.homepage.soptstory.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LikeSopticleResponseDto {
    @Schema(description = "like Id")
    private final Long id;

    @Schema(description = "sopticle Id")
    private final Long sopticleId;

    @Schema(description = "session Id")
    private final String sessionId;

}