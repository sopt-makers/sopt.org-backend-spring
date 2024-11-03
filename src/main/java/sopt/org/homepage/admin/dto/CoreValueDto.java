package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.dao.CoreValueDao;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Schema(description = "핵심 가치")
@Getter
@NoArgsConstructor
public class CoreValueDto {
    @Schema(description = "핵심 가치", example = "용기", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치를 입력해주세요")
    private String value;

    @Schema(description = "핵심 가치 설명", example = "새로운 도전을 위해 과감히 용기내는 사람", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치 설명을 입력해주세요")
    private String description;

    @Schema(description = "핵심 가치 이미지 파일명", example = "image.png", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "핵심 가치 이미지 파일명을 입력해주세요")
    private String imageFileName;

    public CoreValueDao toDao(String image) {
        return CoreValueDao.builder()
                .value(this.value)
                .description(this.description)
                .image(image)
                .build();
    }

    public static List<CoreValueDao> toDaoList(List<CoreValueDto> dtos, List<String> images) {
        if (dtos.size() != images.size()) {
            throw new IllegalArgumentException("DTOs and images lists must have the same size");
        }

        return IntStream.range(0, dtos.size())
                .mapToObj(i -> dtos.get(i).toDao(images.get(i)))
                .collect(Collectors.toList());
    }
}
