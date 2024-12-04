package sopt.org.homepage.review.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import sopt.org.homepage.common.dto.PaginateRequest;
import sopt.org.homepage.common.type.Part;

@Schema(description = "리뷰 조회 요청")
@Getter
public class ReviewsRequestDto extends PaginateRequest {

    @Schema(description = "파트별 필터링(Part, 전체를 불러올땐 아무값도 안넣으면 됩니다.)", nullable = true)
    private final Part part;

    @Schema(description = "기수별 필터링(활동기수로 필터링 합니다, 값을 넣지 않을 경우 전체 조회합니다.)", nullable = true)
    private final Integer generation;

    public ReviewsRequestDto(Integer pageNo, Integer limit, Part part, Integer generation) {
        super(pageNo, limit);
        this.part = part;
        this.generation = generation;
    }
}