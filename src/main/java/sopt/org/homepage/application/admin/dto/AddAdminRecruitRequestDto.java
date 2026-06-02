package sopt.org.homepage.application.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.application.admin.dto.request.main.curriculum.AddAdminPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.introduction.AddAdminPartIntroductionRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.curriculum.AddAdminRecruitPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.question.AddAdminRecruitQuestionRequestDto;

@Validated
@Schema(description = "어드민 모집안내 탭 배포")
@Getter
@NoArgsConstructor
public class AddAdminRecruitRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    @NotNull(message = "generation must not be null")
    @Positive(message = "generation must be a positive number")
    private int generation;

    @Schema(description = "지원하기 헤더 이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "recruit_header.png")
    @NotEmpty(message = "recruitHeaderImageFileName must not be empty")
    private String recruitHeaderImageFileName;

    @Schema(description = "파트별 소개 목록 (한 줄 소개 + 이런 걸 배워요)")
    @Valid
    private List<AddAdminPartIntroductionRequestDto> partIntroduction;

    @Schema(description = "파트별 커리큘럼 목록 (이런 걸 배워요 - 주차별)")
    @Valid
    private List<AddAdminPartCurriculumRequestDto> partCurriculum;

    @Schema(description = "파트별 상세 소개 목록 (파트 소개 + 이런 분이면 좋아요)")
    @Valid
    private List<AddAdminRecruitPartCurriculumRequestDto> recruitPartCurriculum;

    @Schema(description = "자주 묻는 질문 목록")
    @Valid
    private List<AddAdminRecruitQuestionRequestDto> recruitQuestion;
}
