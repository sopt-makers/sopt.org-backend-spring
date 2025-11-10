package sopt.org.homepage.admin.dto.request.main.core;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.main.entity.sub.CoreValueEntity;

import java.util.List;
import java.util.stream.IntStream;

@Schema(description = "핵심 가치")
@Getter
@NoArgsConstructor
public class AddAdminCoreValueRequestDto {
    @Schema(description = "핵심 가치", example = "용기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치를 입력해주세요")
    private String value;

    @Schema(description = "핵심 가치 설명", example = "새로운 도전을 위해 과감히 용기내는 사람", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치 설명을 입력해주세요")
    private String description;

    @Schema(description = "핵심 가치 이미지 파일명", example = "image.png", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치 이미지 파일명을 입력해주세요")
    private String imageFileName;


}
