package sopt.org.homepage.sopticle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "솝티클 생성 요청")
public class CreateSopticleDto {

    @Schema(description = "pg에서 생성한 솝티클 id 입니다., 공홈 SopticleID와 같습니다. ( 추후 Sync가 맞지 않는다면 분리를 해야 할 수도 있을것 같아요)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "솝티클 ID는 필수입니다")
    private Long id;

    @Schema(description = "솝티클 주소", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "솝티클 주소는 필수입니다")
    private String link;

    @Schema(description = "작성자 정보 목록", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "작성자 정보는 필수입니다")
    @Valid
    private List<CreateSopticleAuthorDto> authors;
}