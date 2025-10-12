package sopt.org.homepage.corevalue.service.query.dto;

import lombok.Builder;
import sopt.org.homepage.corevalue.domain.CoreValue;

/**
 * CoreValueView
 *
 * 핵심 가치 조회 DTO
 */
@Builder
public record CoreValueView(
        Long id,
        String value,
        String description,
        String imageUrl,
        Integer displayOrder
) {
    public static CoreValueView from(CoreValue coreValue) {
        return CoreValueView.builder()
                .id(coreValue.getId())
                .value(coreValue.getValue())
                .description(coreValue.getDescription())
                .imageUrl(coreValue.getImageUrl())
                .displayOrder(coreValue.getDisplayOrder())
                .build();
    }

    /**
     * 레거시 API 호환용 (image 필드명 유지)
     */
    public String getImage() {
        return this.imageUrl;
    }
}