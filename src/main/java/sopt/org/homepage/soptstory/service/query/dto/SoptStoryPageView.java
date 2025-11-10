package sopt.org.homepage.soptstory.service.query.dto;

import java.util.List;

/**
 * 페이지네이션된 SoptStory 목록
 *
 * @param content SoptStory 목록
 * @param totalCount 전체 개수
 * @param pageSize 페이지 크기
 * @param currentPage 현재 페이지
 */
public record SoptStoryPageView(
        List<SoptStoryListView> content,
        int totalCount,
        int pageSize,
        int currentPage
) {
    /**
     * 전체 페이지 수 계산
     */
    public int totalPages() {
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    /**
     * 다음 페이지 존재 여부
     */
    public boolean hasNext() {
        return currentPage < totalPages();
    }

    /**
     * 이전 페이지 존재 여부
     */
    public boolean hasPrevious() {
        return currentPage > 1;
    }
}