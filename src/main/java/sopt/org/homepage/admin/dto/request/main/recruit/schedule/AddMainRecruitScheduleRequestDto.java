package sopt.org.homepage.admin.dto.request.main.recruit.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.entity.sub.RecruitScheduleEntity;

import java.util.List;
import java.util.stream.Collectors;

@Schema(description = "모집 일정")
@Getter
@NoArgsConstructor
public class AddMainRecruitScheduleRequestDto {
    @Schema(description = "타입", example = "OB", allowableValues = {"OB", "YB"}, requiredMode = Schema.RequiredMode.REQUIRED)
    @Pattern(regexp = "^(OB|YB)$", message = "타입은 OB 또는 YB만 가능합니다")
    private String type;

    @Schema(description = "일정", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private AddMainScheduleRequestDto schedule;

    public RecruitScheduleEntity toEntity() {
        return RecruitScheduleEntity.builder()
                .type(this.type)
                .schedule(this.schedule.toEntity())
                .build();
    }

    public static List<RecruitScheduleEntity> toEntityList(List<AddMainRecruitScheduleRequestDto> dtos) {
        return dtos.stream()
                .map(AddMainRecruitScheduleRequestDto::toEntity)
                .collect(Collectors.toList());
    }
}

