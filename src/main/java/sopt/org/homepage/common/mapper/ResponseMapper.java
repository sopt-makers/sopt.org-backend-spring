package sopt.org.homepage.common.mapper;

import org.springframework.stereotype.Component;
import sopt.org.homepage.internal.crew.dto.CrewMeetingVo;
import sopt.org.homepage.internal.crew.dto.StudyResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectDetailResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectResponse;
import sopt.org.homepage.internal.playground.dto.Role;
import sopt.org.homepage.project.dto.*;
import sopt.org.homepage.project.dto.type.LinkType;
import sopt.org.homepage.semester.dto.SemesterDao;
import sopt.org.homepage.semester.dto.SemestersListResponse;

import java.util.List;

@Component
public class ResponseMapper {

    public SemestersListResponse.SemestersResponse toSemestersResponse(SemesterDao semester) {
        return new SemestersListResponse.SemestersResponse(
                semester.id(), semester.color(), semester.logo(), semester.background(), semester.name(), semester.year()
        );
    }

    public StudyResponse toStudyResponse(CrewMeetingVo meeting) {
        return new StudyResponse(
                meeting.id(),
                meeting.createdGeneration(),
                meeting.joinableParts(),
                meeting.title(),
                meeting.imageURL().isEmpty() ? null : meeting.imageURL().get(0).url(),
                meeting.startDate(),
                meeting.endDate(),
                meeting.appliedInfo().size()
        );
    }

    public ProjectsResponseDto toProjectResponse(PlaygroundProjectResponse project) {
        List<Link> links = project.links().stream().map(link -> new Link(LinkType.fromValue(link.linkTitle()), link.linkUrl())).toList();
        Category category = new Category(project.category());

        return new ProjectsResponseDto(
                project.id(),
                project.name(),
                project.generation(),
                category,
                project.serviceType(),
                project.summary(),
                project.detail(),
                project.logoImage(),
                project.thumbnailImage(),
                project.isAvailable(),
                project.isFounding(),
                links
        );
    }

    public ProjectDetailResponse toProjectDetailResponse(PlaygroundProjectDetailResponse project) {
        List<Link> links = project.links().stream().map(link -> new Link(LinkType.fromValue(link.linkTitle()), link.linkUrl())).toList();
        Category category = new Category(project.category());
        List<Member> members = project.members().stream().map(member -> new Member(
                member.memberName(), Role.fromValue(member.memberRole()), member.memberDescription()
        )).toList();

        return new ProjectDetailResponse(
                project.id(),
                project.name(),
                project.generation(),
                category,
                project.images().isEmpty() ? "" : project.images().get(0),
                project.serviceType(),
                project.summary(),
                project.detail(),
                project.logoImage(),
                project.thumbnailImage(),
                project.isAvailable(),
                project.isFounding(),
                links,
                project.startAt(),
                project.endAt(),
                project.createdAt(),
                project.updatedAt(),
                members
        );
    }
}