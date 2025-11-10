package sopt.org.homepage.review.service.query.dto;

import sopt.org.homepage.common.type.PartType;

/**
 * 리뷰 검색 조건
 */
public record ReviewSearchCond(
        String category,
        String activity,  // "전체 활동" 카테고리일 때만 사용
        PartType partType,
        Integer generation
) {
}