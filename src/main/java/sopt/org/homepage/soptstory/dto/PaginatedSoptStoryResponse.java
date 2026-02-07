package sopt.org.homepage.soptstory.dto;

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
    public static PaginatedSoptStoryResponse of(
            List<SoptStoryResponse> content,
            int totalCount,
            int pageSize,
            int currentPage
    ) {
        int totalPages = (int) Math.ceil((double) totalCount / pageSize);
        boolean hasNext = currentPage < totalPages;
        boolean hasPrevious = currentPage > 1;

        return new PaginatedSoptStoryResponse(
                content, totalCount, pageSize, currentPage, totalPages, hasNext, hasPrevious
        );
    }
}
