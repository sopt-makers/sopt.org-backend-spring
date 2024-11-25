package sopt.org.homepage.admin.dto.response.main;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.admin.dto.response.main.core.AddAdminCoreValueResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.member.AddAdminMemberResponseRecordDto;

import java.util.List;

@Validated
@Schema(description = "어드민 메인정보 파일 업로드 확인")
@Getter
@Builder
@RequiredArgsConstructor
public class AddAdminConfirmResponseDto {
    @Schema(description = "성공 메세지", requiredMode = Schema.RequiredMode.REQUIRED, example = "success")
    private final String message;
}


