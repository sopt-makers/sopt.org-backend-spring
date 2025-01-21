package sopt.org.homepage.internal.crew.dto;

import jakarta.annotation.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public record CrewMeetingVo(
        Long id,
        Long userId,
        String title,
        String category,
        List<ImageURL> imageURL,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Integer capacity,
        String desc,
        String processDesc,
        LocalDateTime mStartDate,
        LocalDateTime mEndDate,
        String leaderDesc,
        String targetDesc,
        String note,
        Boolean isMentorNeeded,
        Boolean canJoinOnlyActiveGeneration,
        Integer createdGeneration,
        @Nullable
        Integer targetActiveGeneration,
        List<MeetingJoinablePart> joinableParts,
        List<AppliedInfo> appliedInfo,
        User user,
        Integer status
) {}

record User(
        Long id,
        String name,
        Long orgId,
        List<Activity> activities,
        String profileImage,
        String phone
) {}

record Activity(String part, Integer generation) {}