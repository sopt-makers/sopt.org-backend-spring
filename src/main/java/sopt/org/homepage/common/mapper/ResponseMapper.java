package sopt.org.homepage.common.mapper;

import org.springframework.stereotype.Component;
import sopt.org.homepage.review.entity.ReviewEntity;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectDetailResponse;
import sopt.org.homepage.internal.playground.dto.PlaygroundProjectResponseDto;
import sopt.org.homepage.internal.playground.dto.Role;
import sopt.org.homepage.project.dto.record.Category;
import sopt.org.homepage.project.dto.record.Link;
import sopt.org.homepage.project.dto.record.Member;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;
import sopt.org.homepage.project.dto.type.LinkType;
import sopt.org.homepage.review.dto.response.ReviewsResponseDto;
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



    public ProjectsResponseDto toProjectResponse(PlaygroundProjectResponseDto project) {
        List<Link> links = project.links().stream()
                .map(link -> new Link(LinkType.fromValue(link.linkTitle()), link.linkUrl()))
                .toList();
        Category category = new Category(project.category());

        return ProjectsResponseDto.builder()
                .id(project.id())
                .name(project.name())
                .generation(project.generation())
                .category(category)
                .serviceType(project.serviceType())
                .summary(project.summary())
                .detail(project.detail())
                .logoImage(project.logoImage())
                .thumbnailImage(project.thumbnailImage())
                .isAvailable(project.isAvailable())
                .isFounding(project.isFounding())
                .links(links)
                .build();
    }


    public ProjectDetailResponseDto toProjectDetailResponse(PlaygroundProjectDetailResponse project) {
        List<Link> links = project.links().stream()
                .map(link -> new Link(LinkType.fromValue(link.linkTitle()), link.linkUrl()))
                .toList();
        Category category = new Category(project.category());
        List<Member> members = project.members().stream()
                .map(member -> new Member(
                        member.memberName(),
                        Role.fromValue(member.memberRole()),
                        member.memberDescription()
                ))
                .toList();
        String projectImage = project.images().isEmpty() ? null : project.images().get(0);

        return ProjectDetailResponseDto.builder()
                .id(project.id())
                .name(project.name())
                .generation(project.generation())
                .category(category)
                .projectImage(projectImage)
                .serviceType(project.serviceType())
                .summary(project.summary())
                .detail(project.detail())
                .logoImage(project.logoImage())
                .thumbnailImage(project.thumbnailImage())
                .isAvailable(project.isAvailable())
                .isFounding(project.isFounding())
                .links(links)
                .startAt(project.startAt())
                .endAt(project.endAt())
                .uploadedAt(project.createdAt())
                .updatedAt(project.updatedAt())
                .members(members)
                .build();
    }

    public ReviewsResponseDto toReviewResponseDto(ReviewEntity entity) {
        return ReviewsResponseDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .author(entity.getAuthor())
                .authorProfileImageUrl(entity.getAuthorProfileImageUrl())
                .generation(entity.getGeneration())
                .description(entity.getDescription())
                .part(entity.getPart())
                .subject(entity.getSubject())
                .thumbnailUrl(entity.getThumbnailUrl())
                .platform(entity.getPlatform())
                .url(entity.getUrl())
                .build();
    }
}