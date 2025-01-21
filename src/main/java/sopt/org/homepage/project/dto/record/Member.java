package sopt.org.homepage.project.dto.record;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.internal.playground.dto.Role;

import java.util.List;


public record Member(

        @Schema(description = "프로젝트 팀원 이름", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,

        @Schema(description = "프로젝트 팀원의 역할", requiredMode = Schema.RequiredMode.REQUIRED)
        Role role,

        @Schema(description = "프로젝트 팀원의 역할 상세설명", requiredMode = Schema.RequiredMode.REQUIRED)
        String description
){
}


