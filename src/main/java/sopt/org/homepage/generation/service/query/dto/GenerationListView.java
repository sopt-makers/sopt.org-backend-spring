package sopt.org.homepage.generation.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.generation.domain.Generation;

/**
 * GenerationListView
 *
 * 기수 목록 조회 DTO (간략 정보)
 */
@Builder
public record GenerationListView(
        Integer id,
        String name
) {
    public static GenerationListView from(Generation generation) {
        return GenerationListView.builder()
                .id(generation.getId())
                .name(generation.getName())
                .build();
    }
}