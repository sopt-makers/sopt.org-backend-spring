package sopt.org.homepage.corevalue.service.command.dto;

import lombok.Builder;

/**
 * UpdateCoreValueCommand
 *
 * 핵심 가치 수정 커맨드
 */
@Builder
public record UpdateCoreValueCommand(
        Long id,
        String value,
        String description,
        String imageUrl,
        Integer displayOrder
) {
}