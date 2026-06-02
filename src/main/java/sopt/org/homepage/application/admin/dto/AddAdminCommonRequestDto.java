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
import sopt.org.homepage.application.admin.dto.request.main.branding.AddAdminBrandingColorRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.button.AddAdminMainButtonRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.schedule.AddAdminRecruitScheduleRequestDto;

@Validated
@Schema(description = "어드민 공통 탭 배포")
@Getter
@NoArgsConstructor
public class AddAdminCommonRequestDto {

    @Schema(description = "기수", requiredMode = Schema.RequiredMode.REQUIRED, example = "36")
    @NotNull(message = "generation must not be null")
    @Positive(message = "generation must be a positive number")
    private Integer generation;

    @Schema(description = "기수명", requiredMode = Schema.RequiredMode.REQUIRED, example = "SOPT")
    @NotEmpty(message = "name must not be empty")
    private String name;

    @Schema(description = "모집 일정")
    @Valid
    private List<AddAdminRecruitScheduleRequestDto> recruitSchedule;

    @Schema(description = "브랜딩 컬러", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "brandingColor must not be null")
    @Valid
    private AddAdminBrandingColorRequestDto brandingColor;

    @Schema(description = "메인 버튼", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "mainButton must not be null")
    @Valid
    private AddAdminMainButtonRequestDto mainButton;
}
