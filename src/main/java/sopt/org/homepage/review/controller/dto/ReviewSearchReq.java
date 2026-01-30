package sopt.org.homepage.review.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import sopt.org.homepage.global.common.type.PartType;

/**
 * 리뷰 검색 요청 DTO
 * <p>
 * Note: @ModelAttribute 바인딩을 위해 record에서 getter 메서드 필요
 */
@Schema(description = "리뷰 검색 요청")
public record ReviewSearchReq(

        @Schema(description = "카테고리", example = "전체 활동")
        String category,

        @Schema(description = "활동 (전체 활동일 때만)", example = "세미나")
        String activity,

        @Schema(description = "파트", example = "SERVER")
        PartType part,

        @Schema(description = "기수", example = "34")
        Integer generation,

        @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
        Integer pageNo,

        @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
        Integer limit
) {
    /**
     * 기본값을 설정하는 Compact Constructor
     */
    public ReviewSearchReq {
        if (pageNo == null) {
            pageNo = 1;
        }
        if (limit == null) {
            limit = 10;
        }
    }

    /**
     * 페이지네이션 offset 계산
     */
    public long getOffset() {
        return (long) (pageNo - 1) * limit;
    }

    public PartType partType() {
        return part;
    }
}
