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
        int targetGeneration = 33;        // TODO: 현재 34기 데이터가 모이지 않은 관계로 추후 돌려놓아야 함

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
