package sopt.org.homepage.part.service.command.dto;

import lombok.Builder;

import java.util.List;

/**
 * UpdatePartCommand
 *
 * 파트 수정 커맨드
 */
@Builder
public record UpdatePartCommand(
        Long id,
        String description,
        List<String> curriculums
) {
}