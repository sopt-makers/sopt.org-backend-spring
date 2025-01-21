package sopt.org.homepage.internal.crew.dto;

import java.time.LocalDateTime;

public record AppliedInfo(
        Long id,
        Integer type,
        Long meetingId,
        Long userId,
        String content,
        LocalDateTime appliedDate,
        Integer status
) {}
