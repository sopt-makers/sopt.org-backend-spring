package sopt.org.homepage.sopticle.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import sopt.org.homepage.common.type.Part;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "솝티클 생성 응답")
public class CreateSopticleResponseDto {

    @Schema(description = "활동 파트", requiredMode = Schema.RequiredMode.REQUIRED)
    private Part part;

    @Schema(description = "솝티클 썸네일 이미지", requiredMode = Schema.RequiredMode.REQUIRED)
    private String thumbnailUrl;

    @Schema(description = "솝티클 제목", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Schema(description = "솝티클 설명", requiredMode = Schema.RequiredMode.REQUIRED)
    private String description;

    @Schema(description = "활동 기수", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer generation;

    @Schema(description = "작성자 이름", requiredMode = Schema.RequiredMode.REQUIRED)
    private String author;

    @Schema(description = "작성자 프로필 이미지")
    private String authorProfileImageUrl;

    @Schema(description = "솝티클 ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "솝티클 URL", requiredMode = Schema.RequiredMode.REQUIRED)
    private String sopticleUrl;

    @Schema(description = "생성일자", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime uploadedAt;
}
