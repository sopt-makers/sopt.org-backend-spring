package sopt.org.homepage.review.dto.request;


import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;
import sopt.org.homepage.common.dto.PaginateRequest;
import sopt.org.homepage.common.type.Part;

@Validated
@Getter
@Schema(description = "리뷰 조회 요청")
public class ReviewsRequestDto extends PaginateRequest {

    @Parameter(description = "Part, 전체를 불러올땐 아무값도 안넣으면 됩니다.")
    private final Part part;

    @Parameter(description = "활동기수로 필터링 합니다, 값을 넣지 않을 경우 전체 조회합니다.")
    private final Integer generation;

    public ReviewsRequestDto(Integer pageNo, Integer limit, Part part, Integer generation) {
        super(pageNo, limit);
        this.part = part;
        this.generation = generation;
    }
}