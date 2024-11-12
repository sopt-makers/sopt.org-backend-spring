package sopt.org.homepage.admin.dto.request.main.recruit.question;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.main.entity.sub.QuestionEntity;

import java.util.List;

@Schema(description = "질문과 답변")
@Getter
@NoArgsConstructor
public class AddAdminQuestionRequestDto {
    @Schema(description = "질문", example = "몇명 뽑나요?", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "질문을 입력해주세요")
    private String question;

    @Schema(description = "답변", example = "10명 뽑아요.", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "답변을 입력해주세요")
    private String answer;

    public QuestionEntity toEntity() {
        return QuestionEntity.builder()
                .question(this.question)
                .answer(this.answer)
                .build();
    }

    public static List<QuestionEntity> toEntityList(List<AddAdminQuestionRequestDto> dtos) {
        return dtos.stream()
                .map(AddAdminQuestionRequestDto::toEntity)
                .toList();
    }
}
