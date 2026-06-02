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
import sopt.org.homepage.application.admin.dto.request.main.activityschedule.AddAdminActivityScheduleRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.core.AddAdminCoreValueRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.member.AddAdminMemberRequestDto;

@Validated
@Schema(description = "어드민 소개 탭 배포")
@Getter
@NoArgsConstructor
public class AddAdminAboutRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    @NotNull(message = "generation must not be null")
    @Positive(message = "generation must be a positive number")
    private int generation;

    @Schema(description = "헤더 이미지 파일명", requiredMode = Schema.RequiredMode.REQUIRED, example = "header.png")
    @NotEmpty(message = "headerImageFileName must not be empty")
    private String headerImageFileName;

    @Schema(description = "핵심가치 목록")
    @Valid
    private List<AddAdminCoreValueRequestDto> coreValue;

    @Schema(description = "임원진 목록")
    @Valid
    private List<AddAdminMemberRequestDto> member;

    @Schema(description = "전체 일정 목록")
    @Valid
    private List<AddAdminActivityScheduleRequestDto> activitySchedule;
}
