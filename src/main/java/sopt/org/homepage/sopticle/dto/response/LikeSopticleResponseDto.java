package sopt.org.homepage.sopticle.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
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