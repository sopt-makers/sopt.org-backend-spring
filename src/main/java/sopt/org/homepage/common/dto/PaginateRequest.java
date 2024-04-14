package sopt.org.homepage.common.dto;

import jakarta.validation.constraints.Min;
import lombok.Getter;

import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Parameter;

@Getter
public abstract class PaginateRequest {

    @Parameter(description = "페이지", example = "1")
    @Min(value = 1)
    private Integer pageNo = 1;

    @Parameter(description = "페이지별 데이터 개수", example = "10")
    @Min(value = 1)
    private Integer limit = 10;

    public PaginateRequest(@RequestParam(defaultValue = "1") Integer pageNo,
                       @RequestParam(defaultValue = "10") Integer limit) {
        this.pageNo = pageNo;
        this.limit = limit;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = Math.max(pageNo, 1);
    }

    public void setLimit(Integer limit) {
        this.limit = Math.max(limit, 1);
    }

    public Long getOffset() {
        return ((long) (this.pageNo - 1) * this.limit);
    }

}

