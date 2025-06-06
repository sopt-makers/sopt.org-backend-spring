package sopt.org.homepage.main.entity.sub;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BrandingColorEntity {
    @Schema(description = "메인 컬러", example = "#FF0000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String main;

    @Schema(description = "로우 톤 컬러", example = "#CC0000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String low;

    @Schema(description = "하이 톤 컬러", example = "#FF3333", requiredMode = Schema.RequiredMode.REQUIRED)
    private String high;

    @Schema(description = "포인트 컬러", example = "#FF9999", requiredMode = Schema.RequiredMode.REQUIRED)
    private String point;
}
