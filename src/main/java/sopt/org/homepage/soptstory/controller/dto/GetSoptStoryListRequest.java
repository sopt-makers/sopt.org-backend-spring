package sopt.org.homepage.soptstory.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * SoptStory 목록 조회 요청 DTO
 */
public record GetSoptStoryListRequest(
        String sort,           // "likes" or "recent" (기본값)

        @NotNull(message = "페이지 번호는 필수입니다.")
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        Integer pageNo,

        @NotNull(message = "페이지 크기는 필수입니다.")
        @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
        Integer limit
) {
    /**
     * 기본값 설정 생성자
     */
    public GetSoptStoryListRequest {
        if (sort == null || sort.isBlank()) {
            sort = "recent";
        }
        if (pageNo == null) {
            pageNo = 1;
        }
        if (limit == null) {
            limit = 10;
        }
    }
}