package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.dao.PartIntroductionDao;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "파트 소개")
@Getter
@NoArgsConstructor
public class PartIntroductionDto {
    @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트명을 입력해주세요")
    private String part;

    @Schema(description = "파트 설명", example = "Android 앱 개발", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트 설명을 입력해주세요")
    private String description;

    public PartIntroductionDao toDao() {
        return PartIntroductionDao.builder()
                .part(this.part)
                .description(this.description)
                .build();
    }

    public static List<PartIntroductionDao> toDaoList(List<PartIntroductionDto> dtos) {
        return dtos.stream()
                .map(PartIntroductionDto::toDao)
                .collect(Collectors.toList());
    }
}
