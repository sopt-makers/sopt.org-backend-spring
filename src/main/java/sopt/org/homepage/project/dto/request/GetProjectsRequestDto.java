package sopt.org.homepage.project.dto.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.global.common.dto.PaginateRequest;
import sopt.org.homepage.project.dto.type.ProjectType;
import sopt.org.homepage.project.dto.type.ServiceType;

@Schema(description = "프로젝트 목록 조회 조건")
@Validated
@Getter
public class GetProjectsRequestDto extends PaginateRequest {

    @Parameter(description = "필터링 키워드")
    private final ProjectType filter;

    @Parameter(description = "웹/앱 필터링")
    private final ServiceType platform;

    @Setter
    @Parameter(description = "기수")
    private Integer generation;

    @Setter
    @Parameter(description = "서비스 운영 여부")
    private Boolean isAvailable;

    @Setter
    @Parameter(description = "창업 여부")
    private Boolean isFounding;

    @Parameter(description = "정렬 기준: GENERATION 또는 NAME (기본 GENERATION)")
    private SortBy sortBy = SortBy.GENERATION;

    @Parameter(description = "정렬 방향: ASC 또는 DESC (기본 DESC)")
    private SortDirection sortDirection = SortDirection.DESC;

    public enum SortBy { GENERATION, NAME }
    public enum SortDirection { ASC, DESC }

    public void setSortBy(SortBy sortBy) {
        this.sortBy = sortBy != null ? sortBy : SortBy.GENERATION;
    }

    public void setSortDirection(SortDirection sortDirection) {
        this.sortDirection = sortDirection != null ? sortDirection : SortDirection.DESC;
    }

    @Builder
    public GetProjectsRequestDto(Integer pageNo, Integer limit, ProjectType filter, ServiceType platform) {
        super(pageNo, limit);
        this.filter = filter;
        this.platform = platform;
    }
}

