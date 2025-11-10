package sopt.org.homepage.main.service;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.main.dto.response.GetAboutSoptResponseDto;
import sopt.org.homepage.admin.dto.request.main.AddAdminConfirmRequestDto;
import sopt.org.homepage.admin.dto.request.main.AddAdminRequestDto;
import sopt.org.homepage.admin.dto.request.main.GetAdminRequestDto;
import sopt.org.homepage.admin.dto.request.main.core.AddAdminCoreValueRequestDto;
import sopt.org.homepage.admin.dto.request.main.curriculum.AddAdminPartCurriculumRequestDto;
import sopt.org.homepage.admin.dto.request.main.introduction.AddAdminPartIntroductionRequestDto;
import sopt.org.homepage.admin.dto.request.main.member.AddAdminMemberRequestDto;
import sopt.org.homepage.admin.dto.request.main.recruit.curriculum.AddAdminRecruitPartCurriculumRequestDto;
import sopt.org.homepage.admin.dto.request.main.recruit.question.AddAdminRecruitQuestionRequestDto;
import sopt.org.homepage.admin.dto.request.main.recruit.schedule.AddAdminRecruitScheduleRequestDto;
import sopt.org.homepage.admin.dto.request.news.AddAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.DeleteAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.GetAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.response.main.AddAdminResponseDto;
import sopt.org.homepage.admin.dto.response.main.GetAdminResponseDto;
import sopt.org.homepage.admin.dto.response.main.branding.GetAdminBrandingColorResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.button.GetAdminMainButtonResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.core.AddAdminCoreValueResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.core.GetAdminCoreValueResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.curriculum.GetAdminPartCurriculumResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.introduction.GetAdminPartIntroductionResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.member.AddAdminMemberResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.member.GetAdminMemberResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.member.GetAdminSnsLinksResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.news.GetAdminLatestNewsResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.recruit.curriculum.GetAdminIntroductionResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.recruit.curriculum.GetAdminRecruitPartCurriculumResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.recruit.question.GetAdminQuestionResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.recruit.question.GetAdminRecruitQuestionResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.recruit.schedule.GetAdminRecruitScheduleResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.recruit.schedule.GetAdminScheduleResponseRecordDto;
import sopt.org.homepage.admin.dto.response.news.GetAdminNewsResponseDto;
import sopt.org.homepage.aws.s3.S3Service;
import sopt.org.homepage.cache.CacheService;
import sopt.org.homepage.common.constants.CacheType;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.internal.auth.AuthService;
import sopt.org.homepage.internal.crew.CrewService;
import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.main.dto.response.GetAboutPageResponseDto;
import sopt.org.homepage.main.dto.response.GetMainPageResponseDto;
import sopt.org.homepage.main.dto.response.GetRecruitingPageResponseDto;
import sopt.org.homepage.main.dto.response.main.branding.GetMainBrandingColorResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.button.GetMainMainButtonResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.core.GetMainCoreValueResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.curriculum.GetMainPartCurriculumResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.introduction.GetMainPartIntroductionResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.member.GetMainMemberResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.member.GetMainSnsLinksResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.news.GetMainLatestNewsResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.curriculum.GetMainIntroductionResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.curriculum.GetMainRecruitPartCurriculumResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.question.GetMainQuestionResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.question.GetMainRecruitQuestionResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.schedule.GetMainRecruitScheduleResponseRecordDto;
import sopt.org.homepage.main.dto.response.main.recruit.schedule.GetMainScheduleResponseRecordDto;
import sopt.org.homepage.main.entity.MainEntity;
import sopt.org.homepage.main.entity.MainNewsEntity;
import sopt.org.homepage.main.entity.sub.*;
import sopt.org.homepage.main.repository.MainNewsRepository;
import sopt.org.homepage.main.repository.MainRepository;

import java.util.ArrayList;
import java.util.List;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

@RequiredArgsConstructor
@Slf4j
@Service
public class MainServiceImpl implements MainService {
    private static final int MINIMUM_PROJECT_COUNT = 10;

    private final MainRepository mainRepository;
    private final MainNewsRepository mainNewsRepository;
    private final CrewService crewService;
    private final PlaygroundService playgroundService;
    private final AuthService authService;
    private final S3Service s3Service;
    private final CacheService cacheService;


    public AddAdminResponseDto adminAddMainData(AddAdminRequestDto addAdminRequestDto) {
        String baseDir = addAdminRequestDto.getGeneration() + "/";

        MainEntity mainEntity = new MainEntity();
        mainEntity.setGeneration(addAdminRequestDto.getGeneration());
        mainEntity.setName(addAdminRequestDto.getName());

        List<RecruitScheduleEntity> recruitSchedule = AddAdminRecruitScheduleRequestDto.toEntityList(addAdminRequestDto.getRecruitSchedule());
        mainEntity.setRecruitSchedule(recruitSchedule);

        BrandingColorEntity brandingColor = addAdminRequestDto.getBrandingColor().toEntity();
        mainEntity.setBrandingColor(brandingColor);

        MainButtonEntity mainButton = addAdminRequestDto.getMainButton().toEntity();
        mainEntity.setMainButton(mainButton);

        List<PartIntroductionEntity> partIntroduction = AddAdminPartIntroductionRequestDto.toEntityList(addAdminRequestDto.getPartIntroduction());
        mainEntity.setPartIntroduction(partIntroduction);

        mainEntity.setHeaderImage(s3Service.generatePresignedUrl(addAdminRequestDto.getHeaderImageFileName(), baseDir));

        List<String> coreValueImages = new ArrayList<>();
        for (AddAdminCoreValueRequestDto coreValue: addAdminRequestDto.getCoreValue()) {
            String fileName = coreValue.getImageFileName();
            String presignedUrl = s3Service.generatePresignedUrl(fileName, baseDir + "coreValue/");
            coreValueImages.add(presignedUrl);
        }
        List<CoreValueEntity> coreValue = AddAdminCoreValueRequestDto.toEntityList(addAdminRequestDto.getCoreValue(), coreValueImages);
        mainEntity.setCoreValue(coreValue);

        List<PartCurriculumEntity> partCurriculum = AddAdminPartCurriculumRequestDto.toEntityList(addAdminRequestDto.getPartCurriculum());
        mainEntity.setPartCurriculum(partCurriculum);

        List<String> memberProfileImages = new ArrayList<>();
        for (AddAdminMemberRequestDto member: addAdminRequestDto.getMember()) {
            String fileName = member.getProfileImageFileName();
            String presignedUrl = s3Service.generatePresignedUrl(fileName, baseDir + "member/");
            memberProfileImages.add(presignedUrl);
        }
        List<MemberEntity> member = AddAdminMemberRequestDto.toEntityList(addAdminRequestDto.getMember(), memberProfileImages);
        mainEntity.setMember(member);

        mainEntity.setRecruitHeaderImage(s3Service.generatePresignedUrl(addAdminRequestDto.getRecruitHeaderImageFileName(), baseDir));

        List<RecruitPartCurriculumEntity> recruitPartCurriculum = AddAdminRecruitPartCurriculumRequestDto.toEntityList(addAdminRequestDto.getRecruitPartCurriculum());
        mainEntity.setRecruitPartCurriculum(recruitPartCurriculum);

        List<RecruitQuestionEntity> recruitQuestion = AddAdminRecruitQuestionRequestDto.toEntityList(addAdminRequestDto.getRecruitQuestion());
        mainEntity.setRecruitQuestion(recruitQuestion);

        cacheService.put(CacheType.MAIN_ENTITY, String.valueOf(mainEntity.getGeneration()), mainEntity);

        return AddAdminResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .headerImage(mainEntity.getHeaderImage())
                .coreValues(mainEntity.getCoreValue().stream().map(coreValueEntity ->
                        AddAdminCoreValueResponseRecordDto.builder()
                                .value(coreValueEntity.getValue())
                                .image(coreValueEntity.getImage())
                                .build()
                ).toList())
                .members(mainEntity.getMember().stream().map(memberEntity ->
                        AddAdminMemberResponseRecordDto.builder()
                                .role(memberEntity.getRole())
                                .name(memberEntity.getName())
                                .profileImage(memberEntity.getProfileImage())
                                .build()
                ).toList())
                .recruitHeaderImage(mainEntity.getRecruitHeaderImage())
                .build();
    }

    @Override
    public void adminAddMainDataConfirm(AddAdminConfirmRequestDto addAdminConfirmRequestDto) {
        MainEntity mainEntityCache = cacheService.get(CacheType.MAIN_ENTITY, String.valueOf(addAdminConfirmRequestDto.getGeneration()), MainEntity.class);
        if (mainEntityCache == null) {
            throw new ClientBadRequestException("MainEntity Cache not found with generation: " + addAdminConfirmRequestDto.getGeneration());
        }
        // PresignedUrl to Original S3 URL
        mainEntityCache.setHeaderImage(s3Service.getOriginalUrl(mainEntityCache.getHeaderImage()));
        mainEntityCache.setCoreValue(mainEntityCache.getCoreValue().stream().map(coreValueEntity ->
                        CoreValueEntity.builder()
                                .image(s3Service.getOriginalUrl(coreValueEntity.getImage()))
                                .value(coreValueEntity.getValue())
                                .description(coreValueEntity.getDescription())
                                .build())
                .toList());
        mainEntityCache.setMember(mainEntityCache.getMember().stream().map(memberEntity ->
                MemberEntity.builder()
                        .role(memberEntity.getRole())
                        .name(memberEntity.getName())
                        .affiliation(memberEntity.getAffiliation())
                        .introduction(memberEntity.getIntroduction())
                        .profileImage(s3Service.getOriginalUrl(memberEntity.getProfileImage()))
                        .sns(memberEntity.getSns())
                        .build())
                .toList());
        mainEntityCache.setRecruitHeaderImage(s3Service.getOriginalUrl(mainEntityCache.getRecruitHeaderImage()));

        mainRepository.save(mainEntityCache);

        cacheService.evict(CacheType.MAIN_ENTITY, String.valueOf(addAdminConfirmRequestDto.getGeneration()));
    }

    @Transactional
    public GetAdminResponseDto adminGetMain(GetAdminRequestDto getAdminRequestDto) {
        MainEntity mainEntity = mainRepository.findByGeneration(getAdminRequestDto.getGeneration());
        if (mainEntity == null) {
            throw new ClientBadRequestException("MainEntity Cache not found with generation: " + getAdminRequestDto.getGeneration());
        }
        List<MainNewsEntity> mainNewsEntities = mainNewsRepository.findAll();

        return GetAdminResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .name(mainEntity.getName())
                .recruitSchedule(mainEntity.getRecruitSchedule().stream().map(schedule -> GetAdminRecruitScheduleResponseRecordDto.builder()
                        .type(schedule.getType())
                        .schedule(GetAdminScheduleResponseRecordDto.builder()
                            .applicationStartTime(schedule.getSchedule().getApplicationStartTime())
                            .applicationEndTime(schedule.getSchedule().getApplicationEndTime())
                            .applicationResultTime(schedule.getSchedule().getApplicationResultTime())
                            .interviewStartTime(schedule.getSchedule().getInterviewStartTime())
                            .interviewEndTime(schedule.getSchedule().getInterviewEndTime())
                            .finalResultTime(schedule.getSchedule().getFinalResultTime())
                            .build())
                        .build())
                    .toList()
                )
                .brandingColor(GetAdminBrandingColorResponseRecordDto.builder()
                        .main(mainEntity.getBrandingColor().getMain())
                        .high(mainEntity.getBrandingColor().getHigh())
                        .low(mainEntity.getBrandingColor().getLow())
                        .point(mainEntity.getBrandingColor().getPoint())
                        .build())
                .mainButton(GetAdminMainButtonResponseRecordDto.builder()
                        .text(mainEntity.getMainButton().getText())
                        .keyColor(mainEntity.getMainButton().getKeyColor())
                        .subColor(mainEntity.getMainButton().getSubColor())
                        .build())
                .partIntroduction(mainEntity.getPartIntroduction().stream().map(partIntroduction -> GetAdminPartIntroductionResponseRecordDto.builder()
                        .part(partIntroduction.getPart())
                        .description(partIntroduction.getDescription())
                        .build())
                    .toList()
                )
                .latestNews(mainNewsEntities.stream().map(mainNewsEntity -> GetAdminLatestNewsResponseRecordDto.builder()
                        .id(mainNewsEntity.getId())
                        .title(mainNewsEntity.getTitle())
                        .build())
                    .toList()
                )
                .headerImage(mainEntity.getHeaderImage())
                .coreValue(mainEntity.getCoreValue().stream().map(coreValue -> GetAdminCoreValueResponseRecordDto.builder()
                        .value(coreValue.getValue())
                        .description(coreValue.getDescription())
                        .image(coreValue.getImage())
                        .build())
                    .toList()
                )
                .partCurriculum(mainEntity.getPartCurriculum().stream().map(partCurriculum -> GetAdminPartCurriculumResponseRecordDto.builder()
                        .part(partCurriculum.getPart())
                        .curriculums(partCurriculum.getCurriculums())
                        .build())
                    .toList()
                )
                .member(mainEntity.getMember().stream().map(member -> GetAdminMemberResponseRecordDto.builder()
                        .role(member.getRole())
                        .name(member.getName())
                        .affiliation(member.getAffiliation())
                        .introduction(member.getIntroduction())
                        .profileImage(member.getProfileImage())
                        .sns(GetAdminSnsLinksResponseRecordDto.builder()
                                .email(member.getSns().getEmail())
                                .linkedin(member.getSns().getLinkedin())
                                .github(member.getSns().getGithub())
                                .behance(member.getSns().getBehance())
                                .build()
                        )
                        .build())
                    .toList()
                )
                .recruitHeaderImage(mainEntity.getRecruitHeaderImage())
                .recruitPartCurriculum(mainEntity.getRecruitPartCurriculum().stream().map(recruitPartCurriculum -> GetAdminRecruitPartCurriculumResponseRecordDto.builder()
                        .part(recruitPartCurriculum.getPart())
                        .introduction(GetAdminIntroductionResponseRecordDto.builder()
                                .content(recruitPartCurriculum.getIntroduction().getContent())
                                .preference(recruitPartCurriculum.getIntroduction().getPreference())
                                .build())
                        .build())
                    .toList()
                )
                .recruitQuestion(mainEntity.getRecruitQuestion().stream().map(recruitQuestion -> GetAdminRecruitQuestionResponseRecordDto.builder()
                        .part(recruitQuestion.getPart())
                        .questions(recruitQuestion.getQuestions().stream().map(question -> GetAdminQuestionResponseRecordDto.builder()
                                .question(question.getQuestion())
                                .answer(question.getAnswer())
                                .build())
                            .toList()
                        )
                        .build())
                    .toList()
                )
                .build();
    }

    public void adminAddMainNews(AddAdminNewsRequestDto addAdminNewsRequestDto) {
        String imageUrl = s3Service.uploadFile(addAdminNewsRequestDto.getImage(), "news/");
        MainNewsEntity newsEntity = new MainNewsEntity();
        newsEntity.setTitle(addAdminNewsRequestDto.getTitle());
        newsEntity.setLink(addAdminNewsRequestDto.getLink());
        newsEntity.setImage(imageUrl);

        mainNewsRepository.save(newsEntity);
    }

    public void adminDeleteMainNews(DeleteAdminNewsRequestDto deleteAdminNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(deleteAdminNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + deleteAdminNewsRequestDto.getId());
        }

        s3Service.deleteFile(newsEntity.getImage());

        mainNewsRepository.delete(newsEntity);
    }

    public GetAdminNewsResponseDto adminGetMainNews(GetAdminNewsRequestDto getAdminNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(getAdminNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + getAdminNewsRequestDto.getId());
        }

        return GetAdminNewsResponseDto.builder()
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .link(newsEntity.getLink())
                .image(newsEntity.getImage())
                .build();
    }

    public GetMainPageResponseDto getMainPageData() {
        MainEntity mainEntity = mainRepository.findFirstByOrderByGenerationDesc();
        List<MainNewsEntity> mainNewsEntities = mainNewsRepository.findAll();

        return GetMainPageResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .name(mainEntity.getName())
                .brandingColor(GetMainBrandingColorResponseRecordDto.builder()
                        .main(mainEntity.getBrandingColor().getMain())
                        .high(mainEntity.getBrandingColor().getHigh())
                        .low(mainEntity.getBrandingColor().getLow())
                        .point(mainEntity.getBrandingColor().getPoint())
                        .build())
                .mainButton(GetMainMainButtonResponseRecordDto.builder()
                        .text(mainEntity.getMainButton().getText())
                        .keyColor(mainEntity.getMainButton().getKeyColor())
                        .subColor(mainEntity.getMainButton().getSubColor())
                        .build())
                .partIntroduction(mainEntity.getPartIntroduction().stream().map(partIntroduction -> GetMainPartIntroductionResponseRecordDto.builder()
                                .part(partIntroduction.getPart())
                                .description(partIntroduction.getDescription())
                                .build())
                        .toList()
                )
                .latestNews(mainNewsEntities.stream().map(mainNewsEntity -> GetMainLatestNewsResponseRecordDto.builder()
                                .id(mainNewsEntity.getId())
                                .title(mainNewsEntity.getTitle())
                                .image(mainNewsEntity.getImage())
                                .link(mainNewsEntity.getLink())
                                .build())
                        .toList()
                )
                .recruitSchedule(mainEntity.getRecruitSchedule().stream().map(schedule -> GetMainRecruitScheduleResponseRecordDto.builder()
                                .type(schedule.getType())
                                .schedule(GetMainScheduleResponseRecordDto.builder()
                                        .applicationStartTime(schedule.getSchedule().getApplicationStartTime())
                                        .applicationEndTime(schedule.getSchedule().getApplicationEndTime())
                                        .applicationResultTime(schedule.getSchedule().getApplicationResultTime())
                                        .interviewStartTime(schedule.getSchedule().getInterviewStartTime())
                                        .interviewEndTime(schedule.getSchedule().getInterviewEndTime())
                                        .finalResultTime(schedule.getSchedule().getFinalResultTime())
                                        .build())
                                .build())
                        .toList()
                )
                .build();
    }

    public GetAboutPageResponseDto getAboutPageData() {
        MainEntity mainEntity = mainRepository.findFirstByOrderByGenerationDesc();
        return GetAboutPageResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .name(mainEntity.getName())
                .headerImage(mainEntity.getHeaderImage())
                .brandingColor(GetMainBrandingColorResponseRecordDto.builder()
                        .main(mainEntity.getBrandingColor().getMain())
                        .high(mainEntity.getBrandingColor().getHigh())
                        .low(mainEntity.getBrandingColor().getLow())
                        .point(mainEntity.getBrandingColor().getPoint())
                        .build())
                .coreValue(mainEntity.getCoreValue().stream().map(coreValue -> GetMainCoreValueResponseRecordDto.builder()
                                .value(coreValue.getValue())
                                .description(coreValue.getDescription())
                                .image(coreValue.getImage())
                                .build())
                        .toList()
                )
                .partCurriculum(mainEntity.getPartCurriculum().stream().map(partCurriculum -> GetMainPartCurriculumResponseRecordDto.builder()
                                .part(partCurriculum.getPart())
                                .curriculums(partCurriculum.getCurriculums())
                                .build())
                        .toList()
                )
                .member(mainEntity.getMember().stream().map(member -> GetMainMemberResponseRecordDto.builder()
                                .role(member.getRole())
                                .name(member.getName())
                                .affiliation(member.getAffiliation())
                                .introduction(member.getIntroduction())
                                .profileImage(member.getProfileImage())
                                .sns(GetMainSnsLinksResponseRecordDto.builder()
                                        .email(member.getSns().getEmail())
                                        .linkedin(member.getSns().getLinkedin())
                                        .github(member.getSns().getGithub())
                                        .behance(member.getSns().getBehance())
                                        .build()
                                )
                                .build())
                        .toList()
                )
                .activitiesRecords(getActivitiesRecords(mainEntity.getGeneration()))
                .build();
    }

    public GetRecruitingPageResponseDto getRecruitingPageData() {
        MainEntity mainEntity = mainRepository.findFirstByOrderByGenerationDesc();

        return GetRecruitingPageResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .name(mainEntity.getName())
                .recruitHeaderImage(mainEntity.getRecruitHeaderImage())
                .brandingColor(GetMainBrandingColorResponseRecordDto.builder()
                        .main(mainEntity.getBrandingColor().getMain())
                        .high(mainEntity.getBrandingColor().getHigh())
                        .low(mainEntity.getBrandingColor().getLow())
                        .point(mainEntity.getBrandingColor().getPoint())
                        .build())
                .recruitSchedule(mainEntity.getRecruitSchedule().stream().map(schedule -> GetMainRecruitScheduleResponseRecordDto.builder()
                                .type(schedule.getType())
                                .schedule(GetMainScheduleResponseRecordDto.builder()
                                        .applicationStartTime(schedule.getSchedule().getApplicationStartTime())
                                        .applicationEndTime(schedule.getSchedule().getApplicationEndTime())
                                        .applicationResultTime(schedule.getSchedule().getApplicationResultTime())
                                        .interviewStartTime(schedule.getSchedule().getInterviewStartTime())
                                        .interviewEndTime(schedule.getSchedule().getInterviewEndTime())
                                        .finalResultTime(schedule.getSchedule().getFinalResultTime())
                                        .build())
                                .build())
                        .toList()
                )
                .recruitPartCurriculum(mainEntity.getRecruitPartCurriculum().stream().map(recruitPartCurriculum -> GetMainRecruitPartCurriculumResponseRecordDto.builder()
                                .part(recruitPartCurriculum.getPart())
                                .introduction(GetMainIntroductionResponseRecordDto.builder()
                                        .content(recruitPartCurriculum.getIntroduction().getContent())
                                        .preference(recruitPartCurriculum.getIntroduction().getPreference())
                                        .build())
                                .build())
                        .toList()
                )
                .recruitQuestion(mainEntity.getRecruitQuestion().stream().map(recruitQuestion -> GetMainRecruitQuestionResponseRecordDto.builder()
                                .part(recruitQuestion.getPart())
                                .questions(recruitQuestion.getQuestions().stream().map(question -> GetMainQuestionResponseRecordDto.builder()
                                                .question(question.getQuestion())
                                                .answer(question.getAnswer())
                                                .build())
                                        .toList()
                                )
                                .build())
                        .toList()
                )
                .build();
    }

    private GetAboutSoptResponseDto.ActivitiesRecords getActivitiesRecords(int generation) {
        int targetGeneration = determineTargetGeneration(generation);
        var memberStats = getMemberStatistics(targetGeneration);
        var projectCount = getProjectCount(targetGeneration);
        var studyCount = getStudyCount(targetGeneration);

        return new GetAboutSoptResponseDto.ActivitiesRecords(
                memberStats,
                projectCount,
                studyCount
        );
    }

    private int getMemberStatistics(int targetGeneration) {
        return authService.getUserCountByGeneration(targetGeneration);
    }

    private int getProjectCount(int targetGeneration) {
        return findByGeneration(targetGeneration).size();
    }


    private int determineTargetGeneration(int currentGeneration) {
        return findGenerationWithMinimumProjects(currentGeneration, currentGeneration - 5);
    }

    private int findGenerationWithMinimumProjects(int currentGeneration, int minGeneration) {
        if (currentGeneration < minGeneration) {
            return minGeneration;
        }

        var projects = findByGeneration(currentGeneration);
        if (projects.size() >= MINIMUM_PROJECT_COUNT) {
            return currentGeneration;
        }

        return findGenerationWithMinimumProjects(currentGeneration - 1, minGeneration);
    }


    private List<ProjectsResponseDto> findByGeneration(Integer generation) {
        var allProjects = playgroundService.getAllProjects(
                new GetProjectsRequestDto(1, Integer.MAX_VALUE, null, null)
        );
        return allProjects.stream()
                .filter(project -> project.getGeneration() != null &&
                        project.getGeneration().equals(generation))
                .collect(Collectors.toList());
    }


    private Integer getStudyCount(Integer generation) {
        return crewService.getStudyCount(generation);
    }


}
