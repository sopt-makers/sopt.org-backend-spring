package sopt.org.homepage.admin.dto.request.main.branding;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "브랜딩 컬러")
@Getter
@NoArgsConstructor
public class AddAdminBrandingColorRequestDto {
    @Schema(description = "메인 컬러", example = "FF0000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 HEX 컬러 코드를 입력해주세요")
    private String main;

    @Schema(description = "로우 톤 컬러", example = "CC0000", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 HEX 컬러 코드를 입력해주세요")
    private String low;

    @Schema(description = "하이 톤 컬러", example = "FF3333", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 HEX 컬러 코드를 입력해주세요")
    private String high;

    @Schema(description = "포인트 컬러", example = "FF9999", requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 HEX 컬러 코드를 입력해주세요")
    private String point;

}
