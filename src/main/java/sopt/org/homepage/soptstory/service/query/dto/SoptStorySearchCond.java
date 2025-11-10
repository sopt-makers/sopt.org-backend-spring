package sopt.org.homepage.soptstory.service.query.dto;

/**
 * SoptStory 검색 조건 DTO
 *
 * 책임:
 * - 정렬 기준, 페이징 정보 전달
 * - 불변 객체로 안전한 데이터 전달
 *
 * @param sort 정렬 기준 ("likes" or "recent")
 * @param pageNo 페이지 번호 (1부터 시작)
 * @param limit 페이지당 항목 수
 */
public record SoptStorySearchCond(
        String sort,
        int pageNo,
        int limit
) {
    /**
     * offset 계산
     *
     * @return 조회 시작 위치
     */
    public long offset() {
        return (long) (pageNo - 1) * limit;
    }

    /**
     * 정렬 기준 검증 후 반환
     *
     * @return 유효한 정렬 기준 ("likes" or "recent")
     */
    public String sort() {
        if ("likes".equalsIgnoreCase(sort)) {
            return "likes";
        }
        return "recent"; // 기본값
    }
}