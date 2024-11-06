package sopt.org.homepage.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.entity.sub.RecruitQuestionEntity;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "모집 질문")
@Getter
@NoArgsConstructor
public class RecruitQuestionDto {
    @Schema(description = "파트명", example = "안드로이드", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "파트명은 필수입니다")
    private String part;

    @Schema(description = "질문 리스트", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "질문은 필수입니다")
    @Valid
    private List<QuestionDto> questions;

    public RecruitQuestionEntity toEntity() {
        return RecruitQuestionEntity.builder()
                .part(this.part)
                .questions(QuestionDto.toEntityList(this.questions))
                .build();
    }

    public static List<RecruitQuestionEntity> toEntityList(List<RecruitQuestionDto> dtos) {
        return dtos.stream()
                .map(RecruitQuestionDto::toEntity)
                .collect(Collectors.toList());
    }
}

