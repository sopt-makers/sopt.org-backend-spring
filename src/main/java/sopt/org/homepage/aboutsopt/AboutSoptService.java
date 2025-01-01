package sopt.org.homepage.aboutsopt;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.aboutsopt.dto.AboutSoptResponseDto;
import sopt.org.homepage.aboutsopt.dto.CoreValueResponseDto;
import sopt.org.homepage.aboutsopt.dto.GetAboutSoptResponseDto;
import sopt.org.homepage.aboutsopt.entity.AboutSoptEntity;
import sopt.org.homepage.aboutsopt.entity.CoreValueEntity;
import sopt.org.homepage.aboutsopt.repository.AboutSoptRepository;
import sopt.org.homepage.exception.NotFoundException;
import sopt.org.homepage.internal.crew.CrewService;
import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.project.ProjectService;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AboutSoptService {
    private static final int MINIMUM_PROJECT_COUNT = 10;
    private final AboutSoptRepository aboutSoptRepository;
    private final CrewService crewService;
    private final PlaygroundService playgroundService;
    private final ProjectService projectService;

    public GetAboutSoptResponseDto getAboutSopt(Integer generation) {
        AboutSoptEntity aboutSopt = generation != null ?
                aboutSoptRepository.findByIdAndIsPublishedTrue(Long.valueOf(generation))
                        .orElseThrow(() -> new NotFoundException("Not found Published about sopt with id: " + generation))
                : aboutSoptRepository.findTopByIsPublishedTrueOrderByIdDesc()
                        .orElseThrow(() -> new NotFoundException("Not found any published AboutSopt"));

        int targetGeneration = determineTargetGeneration(generation != null ? generation : aboutSopt.getId());

        var members = playgroundService.getAllMembers(targetGeneration);
        var projects = projectService.findByGeneration(targetGeneration);
        var studyCount = crewService.getStudyCount(targetGeneration);

        return GetAboutSoptResponseDto.builder()
                .aboutSopt(convertToResponseDto(aboutSopt))
                .activitiesRecords(GetAboutSoptResponseDto.ActivitiesRecords.builder()
                        .activitiesMemberCount(members.numberOfMembersAtGeneration())
                        .projectCounts(projects.size())
                        .studyCounts(studyCount)
                        .build())
                .build();
    }

    private int determineTargetGeneration(int currentGeneration) {
        return findGenerationWithMinimumProjects(currentGeneration, currentGeneration - 5); // 5기수 전까지(무한 루프 방지)
    }

    private int findGenerationWithMinimumProjects(int currentGeneration, int minGeneration) {
        if (currentGeneration < minGeneration) {
            return minGeneration; // 최소 기수보다 작아지면 최소 기수 반환
        }

        var projects = projectService.findByGeneration(currentGeneration);
        if (projects.size() >= MINIMUM_PROJECT_COUNT) {
            return currentGeneration;
        }

        return findGenerationWithMinimumProjects(currentGeneration - 1, minGeneration);
    }

    private AboutSoptResponseDto convertToResponseDto(AboutSoptEntity entity) {
        return AboutSoptResponseDto.builder()
                .id((long) entity.getId())
                .isPublished(entity.isPublished())
                .title(entity.getTitle())
                .bannerImage(entity.getBannerImage())
                .coreDescription(entity.getCoreDescription())
                .planCurriculum(entity.getPlanCurriculum())
                .designCurriculum(entity.getDesignCurriculum())
                .androidCurriculum(entity.getAndroidCurriculum())
                .iosCurriculum(entity.getIosCurriculum())
                .webCurriculum(entity.getWebCurriculum())
                .serverCurriculum(entity.getServerCurriculum())
                .coreValues(entity.getCoreValues().stream()
                        .map(this::convertToCoreValueDto)
                        .collect(Collectors.toList()))
                .build();
    }

    private CoreValueResponseDto convertToCoreValueDto(CoreValueEntity coreValue) {
        return CoreValueResponseDto.builder()
                .id((long) coreValue.getId())
                .title(coreValue.getTitle())
                .subTitle(coreValue.getSubTitle())
                .imageUrl(coreValue.getImageUrl())
                .build();
    }
}
