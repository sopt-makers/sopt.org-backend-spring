package sopt.org.homepage.corevalue.dto;

import lombok.Builder;
import sopt.org.homepage.corevalue.CoreValue;

/**
 * CreateCoreValueCommand
 * <p>
 * 핵심 가치 생성 커맨드
 */
@Builder
public record CreateCoreValueCommand(
        Integer generationId,
        String value,
        String description,
        String imageUrl,
        Integer displayOrder
) {
    public CoreValue toEntity() {
        return CoreValue.builder()
                .generationId(generationId)
                .value(value)
                .description(description)
                .imageUrl(imageUrl)
                .displayOrder(displayOrder)
                .build();
    }
}
