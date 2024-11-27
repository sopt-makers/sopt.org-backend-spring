package sopt.org.homepage.project.dto;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.common.dto.PaginateRequest;
import sopt.org.homepage.project.dto.type.ProjectType;
import sopt.org.homepage.project.dto.type.ServiceType;


@Validated
@Getter
public class GetProjectsRequestDto extends PaginateRequest {

    @Parameter(description = "필터링 키워드")
    private final ProjectType filter;

    @Parameter(description = "웹/앱 필터링")
    private final ServiceType platform;


    public GetProjectsRequestDto(Integer pageNo, Integer limit, ProjectType filter, ServiceType platform) {
        super(pageNo, limit);
        this.filter = filter;
        this.platform = platform;
    }
}

