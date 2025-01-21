package sopt.org.homepage.sopticle.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import sopt.org.homepage.common.dto.PaginateRequest;
import sopt.org.homepage.common.type.Part;

@Getter
@Schema(description = "Sopticle 조회 요청")
public class GetSopticleListRequestDto extends PaginateRequest {

    @Schema(description = "Part별로 필터링 합니다, 값을 넣지 않을 경우 전체 조회합니다.")
    private final Part part;

    @Schema(description = "활동기수로 필터링 합니다, 값을 넣지 않을 경우 전체 조회합니다.")
    private final Integer generation;

    public GetSopticleListRequestDto(Integer pageNo, Integer limit, Part part, Integer generation) {
        super(pageNo, limit);
        this.part = part;
        this.generation = generation;
    }
}