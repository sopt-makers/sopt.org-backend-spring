package sopt.org.homepage.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class PaginateResponse<T> {

    private final List<T> data;

    @Schema(description = "다음 페이지가 있는지 여부를 나타냄.", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Boolean hasNextPage;

    @Schema(description = "이전 페이지가 있는지 여부를 나타냄.", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Boolean hasPrevPage;

    @Schema(description = "총 data 들의 갯수", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer totalCount;

    @Schema(description = "총 페이지 카운트", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer totalPage;

    @Schema(description = "현재 페이지", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer currentPage;

    @Schema(description = "item을 몇개까지 가져올지에 대한 카운트", requiredMode = Schema.RequiredMode.REQUIRED)
    private final Integer limit;

    public PaginateResponse(List<T> data, Integer totalCount, Integer limit, int currentPage) {
        this.limit = limit;
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil((double) totalCount / limit);
        this.currentPage = currentPage;
        this.data = data;
        this.hasNextPage = this.totalPage > this.currentPage;
        this.hasPrevPage = this.currentPage > 1;
    }

}

