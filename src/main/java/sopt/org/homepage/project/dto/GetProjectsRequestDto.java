package sopt.org.homepage.project.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.common.dto.PaginateRequest;

@Validated
@Getter
public class GetProjectsRequestDto extends PaginateRequest {

    @Parameter(description = "프로젝트 타입")
    private final ProjectType filter;

    @Parameter(description = "서비스 플랫폼")
    private final ServiceType platform;


    public GetProjectsRequestDto(Integer pageNo, Integer limit, ProjectType filter, ServiceType platform) {
        super(pageNo, limit);
        this.filter = filter;
        this.platform = platform;
    }
}

