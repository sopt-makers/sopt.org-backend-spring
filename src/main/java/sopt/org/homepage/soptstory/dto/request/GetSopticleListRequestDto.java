package sopt.org.homepage.soptstory.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import sopt.org.homepage.common.dto.PaginateRequest;

@Getter
@Schema(description = "Sopticle 조회 요청")
public class GetSopticleListRequestDto extends PaginateRequest {

    @Schema(
            description = "정렬 기준: 'latest' 또는 'likes'",
            defaultValue = "latest",
            example = "likes"
    )
    private final String sort;

    public GetSopticleListRequestDto(Integer pageNo, Integer limit, String sort) {
        super(pageNo, limit);
        this.sort = (sort == null || sort.isBlank()) ? "latest" : sort;
    }
}
