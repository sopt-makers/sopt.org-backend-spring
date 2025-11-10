package sopt.org.homepage.soptstory.repository.query;

import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.service.query.dto.SoptStorySearchCond;

import java.util.List;

/**
 * SoptStory Query Repository 인터페이스
 *
 * 책임:
 * - 복잡한 조회 쿼리 (정렬, 페이징, 필터링)
 * - QueryDSL 활용한 동적 쿼리
 */
public interface SoptStoryQueryRepository {

    /**
     * 검색 조건에 따라 SoptStory 목록 조회
     *
     * @param cond 검색 조건 (정렬, 페이징)
     * @return SoptStory 리스트
     */
    List<SoptStory> findAllSorted(SoptStorySearchCond cond);

    /**
     * 전체 SoptStory 개수 조회
     *
     * @return 전체 개수
     */
    long countAll();
}