package sopt.org.homepage.project.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.common.dto.PaginateRequest;
import sopt.org.homepage.project.dto.type.ProjectType;
import sopt.org.homepage.project.dto.type.ServiceType;

@Schema(description = "리뷰 응답")
@Validated
@Getter
public class GetProjectsRequestDto extends PaginateRequest {

    @Parameter(description = "필터링 키워드")
    private final ProjectType filter;

    @Parameter(description = "웹/앱 필터링")
    private final ServiceType platform;

    @Builder
    public GetProjectsRequestDto(Integer pageNo, Integer limit, ProjectType filter, ServiceType platform) {
        super(pageNo, limit);
        this.filter = filter;
        this.platform = platform;
    }
}

