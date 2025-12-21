package sopt.org.homepage.part.service.command.dto;

import java.util.List;
import lombok.Builder;
import sopt.org.homepage.global.common.type.PartType;

/**
 * CreatePartCommand
 * <p>
 * 파트 생성 커맨드
 */
@Builder
public record CreatePartCommand(
        Integer generationId,
        PartType partType,
        String description,
        List<String> curriculums
) {
    public sopt.org.homepage.part.domain.Part toEntity() {
        return sopt.org.homepage.part.domain.Part.builder()
                .generationId(generationId)
                .partType(partType)
                .description(description)
                .curriculums(curriculums)
                .build();
    }
}
