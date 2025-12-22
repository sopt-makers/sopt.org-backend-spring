package sopt.org.homepage.corevalue.dto;

import java.util.List;
import lombok.Builder;

/**
 * BulkCreateCoreValuesCommand
 * <p>
 * 핵심 가치 일괄 생성 커맨드 (Admin용)
 */
@Builder
public record BulkCreateCoreValuesCommand(
        Integer generationId,
        List<CoreValueData> coreValues
) {
    @Builder
    public record CoreValueData(
            String value,
            String description,
            String imageUrl,
            Integer displayOrder
    ) {
    }
}
