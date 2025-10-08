package sopt.org.homepage.soptstory.controller.dto;

import sopt.org.homepage.soptstory.service.query.dto.SoptStoryPageView;

import java.util.List;

/**
 * 페이지네이션된 SoptStory 목록 응답 DTO
 */
public record PaginatedSoptStoryResponse(
        List<SoptStoryResponse> content,
        int totalCount,
        int pageSize,
        int currentPage,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    /**
     * PageView -> Response 변환
     */
    public static PaginatedSoptStoryResponse from(SoptStoryPageView pageView) {
        List<SoptStoryResponse> content = pageView.content().stream()
                .map(SoptStoryResponse::from)
                .toList();

        return new PaginatedSoptStoryResponse(
                content,
                pageView.totalCount(),
                pageView.pageSize(),
                pageView.currentPage(),
                pageView.totalPages(),
                pageView.hasNext(),
                pageView.hasPrevious()
        );
    }
}