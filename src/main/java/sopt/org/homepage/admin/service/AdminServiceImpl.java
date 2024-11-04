package sopt.org.homepage.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.admin.dao.*;
import sopt.org.homepage.admin.dto.*;
import sopt.org.homepage.admin.dto.request.*;
import sopt.org.homepage.admin.dto.response.AddMainResponseDto;
import sopt.org.homepage.admin.dto.response.GetMainNewsResponseDto;
import sopt.org.homepage.admin.dto.response.GetMainResponseDto;
import sopt.org.homepage.admin.dto.response.record.*;
import sopt.org.homepage.admin.entity.MainEntity;
import sopt.org.homepage.admin.entity.MainNewsEntity;
import sopt.org.homepage.admin.repository.MainNewsRepository;
import sopt.org.homepage.admin.repository.MainRepository;
import sopt.org.homepage.aws.s3.S3ServiceImpl;
import sopt.org.homepage.cache.CacheService;
import sopt.org.homepage.common.constants.CacheType;
import sopt.org.homepage.exception.ClientBadRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class AdminServiceImpl implements AdminService{
    private final MainRepository mainRepository;
    private final MainNewsRepository mainNewsRepository;

    private final S3ServiceImpl s3Service;
    private final CacheService cacheService;

    public AddMainResponseDto addMainData(AddMainRequestDto addMainRequestDto) {
        String baseDir = addMainRequestDto.getGeneration() + "/";

        MainEntity mainEntity = new MainEntity();
        mainEntity.setGeneration(addMainRequestDto.getGeneration());
        mainEntity.setName(addMainRequestDto.getName());

        List<RecruitScheduleDao> recruitSchedule = RecruitScheduleDto.toDaoList(addMainRequestDto.getRecruitSchedule());
        mainEntity.setRecruitSchedule(recruitSchedule);

        BrandingColorDao brandingColor = addMainRequestDto.getBrandingColor().toDao();
        mainEntity.setBrandingColor(brandingColor);

        MainButtonDao mainButton = addMainRequestDto.getMainButton().toDao();
        mainEntity.setMainButton(mainButton);

        List<PartIntroductionDao> partIntroduction = PartIntroductionDto.toDaoList(addMainRequestDto.getPartIntroduction());
        mainEntity.setPartIntroduction(partIntroduction);

        mainEntity.setHeaderImage(s3Service.generatePresignedUrl(addMainRequestDto.getHeaderImageFileName(), baseDir));

        List<String> coreValueImages = new ArrayList<>();
        for (CoreValueDto coreValue: addMainRequestDto.getCoreValue()) {
            String fileName = coreValue.getImageFileName();
            String presignedUrl = s3Service.generatePresignedUrl(fileName, baseDir + "coreValue/");
            coreValueImages.add(presignedUrl);
        }
        List<CoreValueDao> coreValue = CoreValueDto.toDaoList(addMainRequestDto.getCoreValue(), coreValueImages);
        mainEntity.setCoreValue(coreValue);

        List<PartCurriculumDao> partCurriculum = PartCurriculumDto.toDaoList(addMainRequestDto.getPartCurriculum());
        mainEntity.setPartCurriculum(partCurriculum);

        List<String> memberProfileImages = new ArrayList<>();
        for (MemberDto member: addMainRequestDto.getMember()) {
            String fileName = member.getProfileImageFileName();
            String presignedUrl = s3Service.generatePresignedUrl(fileName, baseDir + "member/");
            memberProfileImages.add(presignedUrl);
        }
        List<MemberDao> member = MemberDto.toDaoList(addMainRequestDto.getMember(), memberProfileImages);
        mainEntity.setMember(member);

        mainEntity.setRecruitHeaderImage(s3Service.generatePresignedUrl(addMainRequestDto.getRecruitHeaderImageFileName(), baseDir));

        List<RecruitPartCurriculumDao> recruitPartCurriculum = RecruitPartCurriculumDto.toDaoList(addMainRequestDto.getRecruitPartCurriculum());
        mainEntity.setRecruitPartCurriculum(recruitPartCurriculum);

        List<RecruitQuestionDao> recruitQuestion = RecruitQuestionDto.toDaoList(addMainRequestDto.getRecruitQuestion());
        mainEntity.setRecruitQuestion(recruitQuestion);

        cacheService.put(CacheType.MAIN_ENTITY, String.valueOf(mainEntity.getGeneration()), mainEntity);

        return AddMainResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .headerImage(mainEntity.getHeaderImage())
                .coreValues(mainEntity.getCoreValue().stream().map(coreValueDao ->
                        AddMainCoreValueResponseRecordDto.builder()
                                .value(coreValueDao.getValue())
                                .image(coreValueDao.getImage())
                                .build()
                ).collect(Collectors.toList()))
                .members(mainEntity.getMember().stream().map(memberDao ->
                        AddMainMemberResponseRecordDto.builder()
                                .role(memberDao.getRole())
                                .name(memberDao.getName())
                                .profileImage(memberDao.getProfileImage())
                                .build()
                ).collect(Collectors.toList()))
                .recruitHeaderImage(mainEntity.getRecruitHeaderImage())
                .build();
    }

    public void addMainDataConfirm(AddMainConfirmRequestDto addMainConfirmRequestDto) {
        MainEntity mainEntityCache = cacheService.get(CacheType.MAIN_ENTITY, String.valueOf(addMainConfirmRequestDto.getGeneration()), MainEntity.class);
        if (mainEntityCache == null) {
            throw new ClientBadRequestException("MainEntity Cache not found with generation: " + addMainConfirmRequestDto.getGeneration());
        }
        // PresignedUrl to Original S3 URL
        mainEntityCache.setHeaderImage(s3Service.getOriginalUrl(mainEntityCache.getHeaderImage()));
        mainEntityCache.setCoreValue(mainEntityCache.getCoreValue().stream().map(coreValueEntity ->
                        CoreValueDao.builder()
                                .image(s3Service.getOriginalUrl(coreValueEntity.getImage()))
                                .value(coreValueEntity.getValue())
                                .description(coreValueEntity.getDescription())
                                .build())
                .collect(Collectors.toList()));
        mainEntityCache.setMember(mainEntityCache.getMember().stream().map(memberEntity ->
                MemberDao.builder()
                        .role(memberEntity.getRole())
                        .name(memberEntity.getName())
                        .affiliation(memberEntity.getAffiliation())
                        .introduction(memberEntity.getIntroduction())
                        .profileImage(s3Service.getOriginalUrl(memberEntity.getProfileImage()))
                        .sns(memberEntity.getSns())
                        .build())
                .collect(Collectors.toList()));
        mainEntityCache.setRecruitHeaderImage(s3Service.getOriginalUrl(mainEntityCache.getRecruitHeaderImage()));

        mainRepository.save(mainEntityCache);

        cacheService.evict(CacheType.MAIN_ENTITY, String.valueOf(addMainConfirmRequestDto.getGeneration()));

    }
    @Transactional
    public GetMainResponseDto getMain(GetMainRequestDto getMainRequestDto) {
        MainEntity mainEntity = mainRepository.findByGeneration(getMainRequestDto.getGeneration());
        List<MainNewsEntity> mainNewsEntities = mainNewsRepository.findAll();

        return GetMainResponseDto.builder()
                .generation(mainEntity.getGeneration())
                .name(mainEntity.getName())
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
                    .collect(Collectors.toList())
                )
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
                    .collect(Collectors.toList())
                )
                .latestNews(mainNewsEntities.stream().map(mainNewsEntity -> GetMainLatestNewsResponseRecordDto.builder()
                        .id(mainNewsEntity.getId())
                        .title(mainNewsEntity.getTitle())
                        .build())
                    .collect(Collectors.toList())
                )
                .headerImage(mainEntity.getHeaderImage())
                .coreValue(mainEntity.getCoreValue().stream().map(coreValue -> GetMainCoreValueResponseRecordDto.builder()
                        .value(coreValue.getValue())
                        .description(coreValue.getDescription())
                        .image(coreValue.getImage())
                        .build())
                    .collect(Collectors.toList())
                )
                .partCurriculum(mainEntity.getPartCurriculum().stream().map(partCurriculum -> GetMainPartCurriculumResponseRecordDto.builder()
                        .part(partCurriculum.getPart())
                        .weeks(partCurriculum.getWeeks().stream().map(curriculumWeek -> GetMainCurriculumWeekResponseRecordDto.builder()
                                .week(curriculumWeek.getWeek())
                                .description(curriculumWeek.getDescription())
                                .build())
                            .collect(Collectors.toList())
                        )
                        .build())
                    .collect(Collectors.toList())
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
                    .collect(Collectors.toList())
                )
                .recruitHeaderImage(mainEntity.getRecruitHeaderImage())
                .recruitPartCurriculum(mainEntity.getRecruitPartCurriculum().stream().map(recruitPartCurriculum -> GetMainRecruitPartCurriculumResponseRecordDto.builder()
                        .part(recruitPartCurriculum.getPart())
                        .introduction(GetMainIntroductionResponseRecordDto.builder()
                                .content(recruitPartCurriculum.getIntroduction().getContent())
                                .preference(recruitPartCurriculum.getIntroduction().getPreference())
                                .build())
                        .build())
                    .collect(Collectors.toList())
                )
                .recruitQuestion(mainEntity.getRecruitQuestion().stream().map(recruitQuestion -> GetMainRecruitQuestionResponseRecordDto.builder()
                        .part(recruitQuestion.getPart())
                        .questions(recruitQuestion.getQuestions().stream().map(question -> GetMainQuestionResponseRecordDto.builder()
                                .question(question.getQuestion())
                                .answer(question.getAnswer())
                                .build())
                            .collect(Collectors.toList())
                        )
                        .build())
                    .collect(Collectors.toList())
                )
                .build();
    }


    public void addMainNews(AddMainNewsRequestDto addMainNewsRequestDto) {
        String imageUrl = s3Service.uploadFile(addMainNewsRequestDto.getImage(), "news/");
        MainNewsEntity newsEntity = new MainNewsEntity();
        newsEntity.setTitle(addMainNewsRequestDto.getTitle());
        newsEntity.setLink(addMainNewsRequestDto.getLink());
        newsEntity.setImage(imageUrl);

        mainNewsRepository.save(newsEntity);
    }

    public void deleteMainNews(DeleteMainNewsRequestDto deleteMainNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(deleteMainNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + deleteMainNewsRequestDto.getId());
        }

        s3Service.deleteFile(newsEntity.getImage());

        mainNewsRepository.delete(newsEntity);
    }

    public GetMainNewsResponseDto getMainNews(GetMainNewsRequestDto getMainNewsRequestDto) {
        MainNewsEntity newsEntity = mainNewsRepository.findById(getMainNewsRequestDto.getId());
        if (newsEntity == null) {
            throw new ClientBadRequestException("News not found with id: " + getMainNewsRequestDto.getId());
        }

        return GetMainNewsResponseDto.builder()
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .link(newsEntity.getLink())
                .image(newsEntity.getImage())
                .build();
    }

//    public GetNotificationListResponseDto getNotificationEmailList(int generation) {
//        List<String> emailList = notificationRepository.findByGeneration(generation).stream()
//                .map(MainEntity::getEmail)
//                .collect(Collectors.toList());
//
//        return new GetNotificationListResponseDto(generation, emailList);
//    }


}
