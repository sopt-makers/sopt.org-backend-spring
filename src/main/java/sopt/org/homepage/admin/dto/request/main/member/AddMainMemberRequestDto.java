package sopt.org.homepage.admin.dto.request.main.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sopt.org.homepage.admin.entity.sub.MemberEntity;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Schema(description = "멤버 정보")
@Getter
@NoArgsConstructor
public class AddMainMemberRequestDto {
    @Schema(description = "역할", example = "회장", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "역할을 입력해주세요")
    private String role;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @Schema(description = "소속", example = "SOPT", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "소속을 입력해주세요")
    private String affiliation;

    @Schema(description = "한줄 소개", example = "안녕하세요!", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "한줄 소개를 입력해주세요")
    private String introduction;

    @Schema(description = "SNS 링크", requiredMode = Schema.RequiredMode.REQUIRED)
    @Valid
    private AddMainSnsLinksRequestDto sns;

    @Schema(description = "프로필 이미지 파일명", example = "image.png", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "프로필 이미지 파일명을 입력해주세요")
    private String profileImageFileName;

    public MemberEntity toEntity(String profileImage) {
        return MemberEntity.builder()
                .role(this.role)
                .name(this.name)
                .affiliation(this.affiliation)
                .introduction(this.introduction)
                .sns(this.sns.toEntity())
                .profileImage(profileImage)
                .build();
    }

    public static List<MemberEntity> toEntityList(List<AddMainMemberRequestDto> dtos, List<String> images) {
        if (dtos.size() != images.size()) {
            throw new IllegalArgumentException("DTOs and images lists must have the same size");
        }

        return IntStream.range(0, dtos.size())
                .mapToObj(i -> dtos.get(i).toEntity(images.get(i)))
                .collect(Collectors.toList());
    }
}

