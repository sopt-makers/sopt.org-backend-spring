package sopt.org.homepage.application.admin.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.application.admin.dto.request.main.curriculum.AddAdminPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.introduction.AddAdminPartIntroductionRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.curriculum.AddAdminRecruitPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.question.AddAdminRecruitQuestionRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.schedule.AddAdminRecruitScheduleRequestDto;
import sopt.org.homepage.application.admin.dto.response.main.branding.GetAdminBrandingColorResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.button.GetAdminMainButtonResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.core.AddAdminCoreValueResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.core.GetAdminCoreValueResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.curriculum.GetAdminPartCurriculumResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.introduction.GetAdminPartIntroductionResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.member.AddAdminMemberResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.member.GetAdminMemberResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.member.GetAdminSnsLinksResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.news.GetAdminLatestNewsResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.curriculum.GetAdminIntroductionResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.curriculum.GetAdminRecruitPartCurriculumResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.question.GetAdminQuestionResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.question.GetAdminRecruitQuestionResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.schedule.GetAdminRecruitScheduleResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.recruit.schedule.GetAdminScheduleResponseRecordDto;
import sopt.org.homepage.corevalue.CoreValueService;
import sopt.org.homepage.corevalue.dto.BulkCreateCoreValuesCommand;
import sopt.org.homepage.corevalue.dto.CoreValueView;
import sopt.org.homepage.faq.FAQService;
import sopt.org.homepage.faq.dto.BulkCreateFAQsCommand;
import sopt.org.homepage.faq.dto.FAQView;
import sopt.org.homepage.generation.GenerationService;
import sopt.org.homepage.generation.dto.CreateGenerationCommand;
import sopt.org.homepage.generation.dto.GenerationDetailView;
import sopt.org.homepage.global.common.constants.CacheType;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.infrastructure.aws.s3.S3Service;
import sopt.org.homepage.infrastructure.cache.CacheService;
import sopt.org.homepage.member.MemberService;
import sopt.org.homepage.member.dto.BulkCreateMembersCommand;
import sopt.org.homepage.member.dto.MemberDetailView;
import sopt.org.homepage.news.MainNewsEntity;
import sopt.org.homepage.news.controller.dto.request.AddAdminConfirmRequestDto;
import sopt.org.homepage.news.controller.dto.request.AddAdminRequestDto;
import sopt.org.homepage.news.controller.dto.request.GetAdminRequestDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminConfirmResponseDto;
import sopt.org.homepage.news.controller.dto.response.AddAdminResponseDto;
import sopt.org.homepage.news.controller.dto.response.GetAdminResponseDto;
import sopt.org.homepage.news.repository.MainNewsRepository;
import sopt.org.homepage.part.PartService;
import sopt.org.homepage.part.dto.BulkCreatePartsCommand;
import sopt.org.homepage.part.dto.PartDetailView;
import sopt.org.homepage.recruitment.RecruitmentService;
import sopt.org.homepage.recruitment.dto.BulkCreateRecruitmentsCommand;
import sopt.org.homepage.recruitment.dto.RecruitmentView;
import sopt.org.homepage.recruitpartintroduction.RecruitPartIntroductionService;
import sopt.org.homepage.recruitpartintroduction.dto.BulkCreateRecruitPartIntroductionsCommand;
import sopt.org.homepage.recruitpartintroduction.dto.RecruitPartIntroductionView;


/**
 * AdminServiceImpl
 * <p>
 * 책임: - Admin API 처리 - 여러 도메인의 Command Service 조합 - S3 Presigned URL 생성 및 캐시 관리 - 레거시 의존성 제거 완료
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final CoreValueService coreValueService;
    private final FAQService faqService;
    private final GenerationService generationService;
    private final MemberService memberService;
    private final PartService partService;
    private final RecruitmentService recruitmentService;
    private final RecruitPartIntroductionService recruitPartIntroductionService;


    // ===== Infrastructure Services =====
    private final S3Service s3Service;
    private final CacheService cacheService;
    private final MainNewsRepository mainNewsRepository;

    /**
     * Admin 메인 데이터 배포 (1단계)
     * <p>
     * Presigned URL 생성 및 캐시 저장
     */
    @Override
    @Transactional
    public AddAdminResponseDto addMainData(AddAdminRequestDto request) {
        log.info("Adding admin main data for generation: {}", request.getGeneration());

        Integer generationId = request.getGeneration();
        String baseDir = generationId + "/";

        // ===== 1. Generation 데이터 준비 =====
        CachedAdminData cachedData = new CachedAdminData();
        cachedData.setGenerationId(generationId);
        cachedData.setName(request.getName());

        // Header Image Presigned URL
        String headerImageUrl = s3Service.generatePresignedUrl(
                request.getHeaderImageFileName(), baseDir);
        cachedData.setHeaderImageUrl(headerImageUrl);

        String recruitHeaderImageUrl = s3Service.generatePresignedUrl(
                request.getRecruitHeaderImageFileName(), baseDir);
        cachedData.setRecruitHeaderImageUrl(recruitHeaderImageUrl);

        // BrandingColor
        var brandingColorDto = request.getBrandingColor();
        cachedData.setBrandingColorMain(brandingColorDto.getMain());
        cachedData.setBrandingColorLow(brandingColorDto.getLow());
        cachedData.setBrandingColorHigh(brandingColorDto.getHigh());
        cachedData.setBrandingColorPoint(brandingColorDto.getPoint());

        // MainButton
        var mainButtonDto = request.getMainButton();
        cachedData.setMainButtonText(mainButtonDto.getText());
        cachedData.setMainButtonKeyColor(mainButtonDto.getKeyColor());
        cachedData.setMainButtonSubColor(mainButtonDto.getSubColor());

        // ===== 2. CoreValue Presigned URLs =====
        List<CachedAdminData.CoreValueData> coreValueDataList = new ArrayList<>();
        for (var cv : request.getCoreValue()) {
            String imageUrl = s3Service.generatePresignedUrl(
                    cv.getImageFileName(), baseDir + "coreValue/");

            coreValueDataList.add(CachedAdminData.CoreValueData.builder()
                    .value(cv.getValue())
                    .description(cv.getDescription())
                    .imageUrl(imageUrl)
                    .build());
        }
        cachedData.setCoreValues(coreValueDataList);

        // ===== 3. Member Presigned URLs =====
        List<CachedAdminData.MemberData> memberDataList = new ArrayList<>();
        for (var member : request.getMember()) {
            String profileImageUrl = s3Service.generatePresignedUrl(
                    member.getProfileImageFileName(), baseDir + "member/");

            memberDataList.add(CachedAdminData.MemberData.builder()
                    .role(member.getRole())
                    .name(member.getName())
                    .affiliation(member.getAffiliation())
                    .introduction(member.getIntroduction())
                    .profileImageUrl(profileImageUrl)
                    .snsEmail(member.getSns() != null ? member.getSns().getEmail() : null)
                    .snsLinkedin(member.getSns() != null ? member.getSns().getLinkedin() : null)
                    .snsGithub(member.getSns() != null ? member.getSns().getGithub() : null)
                    .snsBehance(member.getSns() != null ? member.getSns().getBehance() : null)
                    .build());
        }
        cachedData.setMembers(memberDataList);

        // ===== 4. 나머지 데이터 저장 (DTO 그대로 저장) =====
        cachedData.setPartIntroductions(new ArrayList<>(request.getPartIntroduction()));
        cachedData.setPartCurriculums(new ArrayList<>(request.getPartCurriculum()));
        cachedData.setRecruitSchedules(new ArrayList<>(request.getRecruitSchedule()));
        cachedData.setRecruitPartCurriculums(new ArrayList<>(request.getRecruitPartCurriculum()));
        cachedData.setRecruitQuestions(new ArrayList<>(request.getRecruitQuestion()));

        // ===== 5. 캐시에 저장 =====
        cacheService.put(CacheType.ADMIN_MAIN_DATA, String.valueOf(generationId), cachedData);

        log.info("Admin main data cached for generation: {}", generationId);

        // ===== 6. Response 생성 =====
        return AddAdminResponseDto.builder()
                .generation(generationId)
                .headerImage(headerImageUrl)
                .coreValues(coreValueDataList.stream()
                        .map(cv -> AddAdminCoreValueResponseRecordDto.builder()
                                .value(cv.getValue())
                                .image(cv.getImageUrl())
                                .build())
                        .toList())
                .members(memberDataList.stream()
                        .map(m -> AddAdminMemberResponseRecordDto.builder()
                                .role(m.getRole())
                                .name(m.getName())
                                .profileImage(m.getProfileImageUrl())
                                .build())
                        .toList())
                .recruitHeaderImage(recruitHeaderImageUrl)
                .build();
    }

    /**
     * Admin 메인 데이터 배포 확인 (2단계)
     * <p>
     * 캐시에서 데이터를 읽어 실제 DB에 저장
     */
    @Override
    @Transactional
    public AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto request) {
        log.info("Confirming admin main data for generation: {}", request.getGeneration());

        Integer generationId = request.getGeneration();

        // ===== 1. 캐시에서 데이터 조회 =====
        CachedAdminData cachedData = cacheService.get(
                CacheType.ADMIN_MAIN_DATA,
                String.valueOf(generationId),
                CachedAdminData.class);

        if (cachedData == null) {
            throw new ClientBadRequestException(
                    "Cached admin data not found for generation: " + generationId);
        }

        // ===== 2. Presigned URL → S3 Original URL 변환 =====
        String headerImageUrl = s3Service.getOriginalUrl(cachedData.getHeaderImageUrl());
        String recruitHeaderImageUrl = s3Service.getOriginalUrl(cachedData.getRecruitHeaderImageUrl());

        List<String> coreValueImageUrls = cachedData.getCoreValues().stream()
                .map(cv -> s3Service.getOriginalUrl(cv.getImageUrl()))
                .toList();

        List<String> memberProfileImageUrls = cachedData.getMembers().stream()
                .map(m -> s3Service.getOriginalUrl(m.getProfileImageUrl()))
                .toList();

        // ===== 3. Generation 생성 =====
        generationService.create(
                CreateGenerationCommand.builder()
                        .id(generationId)
                        .name(cachedData.getName())
                        .headerImage(headerImageUrl)
                        .recruitHeaderImage(recruitHeaderImageUrl)
                        .brandingColor(CreateGenerationCommand.BrandingColorCommand.builder()
                                .main(cachedData.getBrandingColorMain())
                                .sub(cachedData.getBrandingColorLow())
                                .point(cachedData.getBrandingColorHigh())
                                .background(cachedData.getBrandingColorPoint())
                                .build())
                        .mainButton(CreateGenerationCommand.MainButtonCommand.builder()
                                .text(cachedData.getMainButtonText())
                                .keyColor(cachedData.getMainButtonKeyColor())
                                .subColor(cachedData.getMainButtonSubColor())
                                .build())
                        .build()
        );

        // ===== 4. CoreValue 일괄 생성 =====
        List<BulkCreateCoreValuesCommand.CoreValueData> coreValueDataList = new ArrayList<>();
        for (int i = 0; i < cachedData.getCoreValues().size(); i++) {
            var cv = cachedData.getCoreValues().get(i);
            coreValueDataList.add(BulkCreateCoreValuesCommand.CoreValueData.builder()
                    .value(cv.getValue())
                    .description(cv.getDescription())
                    .imageUrl(coreValueImageUrls.get(i))
                    .displayOrder(i)
                    .build());
        }
        coreValueService.bulkCreate(
                BulkCreateCoreValuesCommand.builder()
                        .generationId(generationId)
                        .coreValues(coreValueDataList)
                        .build()
        );

        // ===== 5. Member 일괄 생성 =====
        List<BulkCreateMembersCommand.MemberData> memberDataList = new ArrayList<>();
        for (int i = 0; i < cachedData.getMembers().size(); i++) {
            var m = cachedData.getMembers().get(i);
            memberDataList.add(BulkCreateMembersCommand.MemberData.builder()
                    .role(m.getRole())
                    .name(m.getName())
                    .affiliation(m.getAffiliation())
                    .introduction(m.getIntroduction())
                    .profileImageUrl(memberProfileImageUrls.get(i))
                    .snsLinks(BulkCreateMembersCommand.SnsLinksData.builder()
                            .email(m.getSnsEmail())
                            .linkedin(m.getSnsLinkedin())
                            .github(m.getSnsGithub())
                            .behance(m.getSnsBehance())
                            .build())
                    .build());
        }
        memberService.bulkCreate(
                BulkCreateMembersCommand.builder()
                        .generationId(generationId)
                        .members(memberDataList)
                        .build()
        );

        // ===== 6. Part 일괄 생성 =====
        partService.bulkCreate(
                BulkCreatePartsCommand.builder()
                        .generationId(generationId)
                        .partIntroductions(cachedData.getPartIntroductions().stream()
                                .map(pi -> BulkCreatePartsCommand.PartData.builder()
                                        .part(pi.getPart())
                                        .description(pi.getDescription())
                                        .build())
                                .toList())
                        .partCurriculums(cachedData.getPartCurriculums().stream()
                                .map(pc -> BulkCreatePartsCommand.PartCurriculumData.builder()
                                        .part(pc.getPart())
                                        .curriculums(pc.getCurriculums())
                                        .build())
                                .toList())
                        .build()
        );

        // ===== 7. Recruitment 일괄 생성 =====
        recruitmentService.bulkCreate(
                BulkCreateRecruitmentsCommand.builder()
                        .generationId(generationId)
                        .recruitments(cachedData.getRecruitSchedules().stream()
                                .map(rs -> BulkCreateRecruitmentsCommand.RecruitmentData.builder()
                                        .type(rs.getType())
                                        .schedule(BulkCreateRecruitmentsCommand.ScheduleData.builder()
                                                .applicationStartTime(rs.getSchedule().getApplicationStartTime())
                                                .applicationEndTime(rs.getSchedule().getApplicationEndTime())
                                                .applicationResultTime(rs.getSchedule().getApplicationResultTime())
                                                .interviewStartTime(rs.getSchedule().getInterviewStartTime())
                                                .interviewEndTime(rs.getSchedule().getInterviewEndTime())
                                                .finalResultTime(rs.getSchedule().getFinalResultTime())
                                                .build())
                                        .build())
                                .toList())
                        .build()
        );

        // ===== 8. RecruitPartIntroduction 일괄 생성 =====
        recruitPartIntroductionService.bulkCreate(
                BulkCreateRecruitPartIntroductionsCommand.builder()
                        .generationId(generationId)
                        .partIntroductions(cachedData.getRecruitPartCurriculums().stream()
                                .map(rpc -> BulkCreateRecruitPartIntroductionsCommand.PartIntroductionData.builder()
                                        .part(rpc.getPart())
                                        .introduction(
                                                BulkCreateRecruitPartIntroductionsCommand.IntroductionData.builder()
                                                        .content(rpc.getIntroduction().getContent())
                                                        .preference(rpc.getIntroduction().getPreference())
                                                        .build())
                                        .build())
                                .toList())
                        .build()
        );

        // ===== 9. FAQ 일괄 생성 =====
        faqService.bulkCreate(
                BulkCreateFAQsCommand.builder()
                        .faqs(cachedData.getRecruitQuestions().stream()
                                .map(rq -> BulkCreateFAQsCommand.FAQData.builder()
                                        .part(rq.getPart())
                                        .question(rq.getQuestions().stream()
                                                .map(q -> BulkCreateFAQsCommand.QuestionData.builder()
                                                        .question(q.getQuestion())
                                                        .answer(q.getAnswer())
                                                        .build())
                                                .toList())
                                        .build())
                                .toList())
                        .build()
        );

        // ===== 10. 캐시 삭제 =====
        cacheService.evict(CacheType.ADMIN_MAIN_DATA, String.valueOf(generationId));

        log.info("Admin main data confirmed for generation: {}", generationId);

        return AddAdminConfirmResponseDto.builder()
                .message("파일 업로드 확인 및 어드민 데이터 배포 성공")
                .build();
    }

    /**
     * Admin 메인 데이터 조회
     */
    @Override
    @Transactional(readOnly = true)
    public GetAdminResponseDto getMain(GetAdminRequestDto request) {
        log.info("Getting admin main data for generation: {}", request.getGeneration());

        Integer generationId = request.getGeneration();

        // ===== 각 도메인에서 데이터 조회 =====
        GenerationDetailView generation = generationService.findById(generationId);
        List<CoreValueView> coreValues = coreValueService.findByGeneration(generationId);
        List<MemberDetailView> members = memberService.findByGeneration(generationId);
        List<PartDetailView> parts = partService.findByGeneration(generationId);
        List<RecruitmentView> recruitments = recruitmentService.findByGeneration(generationId);
        List<RecruitPartIntroductionView> recruitPartIntros =
                recruitPartIntroductionService.findByGeneration(generationId);
        List<FAQView> faqs = faqService.findAll();
        List<MainNewsEntity> mainNewsEntities = mainNewsRepository.findAll();

        // ===== Response 조합 =====
        return GetAdminResponseDto.builder()
                .generation(generation.id())
                .name(generation.name())
                .recruitSchedule(recruitments.stream()
                        .map(r -> GetAdminRecruitScheduleResponseRecordDto.builder()
                                .type(r.type())
                                .schedule(GetAdminScheduleResponseRecordDto.builder()
                                        .applicationStartTime(r.schedule().applicationStartTime())
                                        .applicationEndTime(r.schedule().applicationEndTime())
                                        .applicationResultTime(r.schedule().applicationResultTime())
                                        .interviewStartTime(r.schedule().interviewStartTime())
                                        .interviewEndTime(r.schedule().interviewEndTime())
                                        .finalResultTime(r.schedule().finalResultTime())
                                        .build())
                                .build())
                        .toList())
                .brandingColor(GetAdminBrandingColorResponseRecordDto.builder()
                        .main(generation.brandingColor().main())
                        .high(generation.brandingColor().high())
                        .low(generation.brandingColor().low())
                        .point(generation.brandingColor().background())
                        .build())
                .mainButton(GetAdminMainButtonResponseRecordDto.builder()
                        .text(generation.mainButton().text())
                        .keyColor(generation.mainButton().keyColor())
                        .subColor(generation.mainButton().subColor())
                        .build())
                .partIntroduction(parts.stream()
                        .map(p -> GetAdminPartIntroductionResponseRecordDto.builder()
                                .part(p.part())
                                .description(p.description())
                                .build())
                        .toList())
                .latestNews(mainNewsEntities.stream()
                        .map(news -> GetAdminLatestNewsResponseRecordDto.builder()
                                .id(news.getId())
                                .title(news.getTitle())
                                .build())
                        .toList())
                .headerImage(generation.headerImage())
                .coreValue(coreValues.stream()
                        .map(cv -> GetAdminCoreValueResponseRecordDto.builder()
                                .value(cv.value())
                                .description(cv.description())
                                .image(cv.imageUrl())
                                .build())
                        .toList())
                .partCurriculum(parts.stream()
                        .map(p -> GetAdminPartCurriculumResponseRecordDto.builder()
                                .part(p.part())
                                .curriculums(p.curriculums())
                                .build())
                        .toList())
                .member(members.stream()
                        .map(m -> GetAdminMemberResponseRecordDto.builder()
                                .role(m.role())
                                .name(m.name())
                                .affiliation(m.affiliation())
                                .introduction(m.introduction())
                                .profileImage(m.profileImageUrl())
                                .sns(GetAdminSnsLinksResponseRecordDto.builder()
                                        .email(m.snsLinks().email())
                                        .linkedin(m.snsLinks().linkedin())
                                        .github(m.snsLinks().github())
                                        .behance(m.snsLinks().behance())
                                        .build())
                                .build())
                        .toList())
                .recruitHeaderImage(generation.recruitHeaderImage())
                .recruitPartCurriculum(recruitPartIntros.stream()
                        .map(rpi -> GetAdminRecruitPartCurriculumResponseRecordDto.builder()
                                .part(rpi.part())
                                .introduction(GetAdminIntroductionResponseRecordDto.builder()
                                        .content(rpi.introduction().content())
                                        .preference(rpi.introduction().preference())
                                        .build())
                                .build())
                        .toList())
                .recruitQuestion(faqs.stream()
                        .map(faq -> GetAdminRecruitQuestionResponseRecordDto.builder()
                                .part(faq.part().getValue())
                                .questions(faq.questions().stream()  // ✅ questions() 복수형!
                                        .map(q -> GetAdminQuestionResponseRecordDto.builder()
                                                .question(q.question())
                                                .answer(q.answer())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    // ===== 캐시용 데이터 클래스 =====

    @lombok.Data
    public static class CachedAdminData implements java.io.Serializable {
        // Generation
        private Integer generationId;
        private String name;
        private String headerImageUrl;
        private String recruitHeaderImageUrl;

        // BrandingColor
        private String brandingColorMain;
        private String brandingColorLow;
        private String brandingColorHigh;
        private String brandingColorPoint;

        // MainButton
        private String mainButtonText;
        private String mainButtonKeyColor;
        private String mainButtonSubColor;

        // CoreValue
        private List<CoreValueData> coreValues;

        // Member
        private List<MemberData> members;

        // Part - ✅ 구체적인 타입 사용
        private List<AddAdminPartIntroductionRequestDto> partIntroductions;
        private List<AddAdminPartCurriculumRequestDto> partCurriculums;

        // Recruitment - ✅ 구체적인 타입 사용
        private List<AddAdminRecruitScheduleRequestDto> recruitSchedules;
        private List<AddAdminRecruitPartCurriculumRequestDto> recruitPartCurriculums;

        // FAQ - ✅ 구체적인 타입 사용
        private List<AddAdminRecruitQuestionRequestDto> recruitQuestions;

        @lombok.Builder
        @lombok.Data
        public static class CoreValueData implements java.io.Serializable {
            private String value;
            private String description;
            private String imageUrl;
        }

        @lombok.Builder
        @lombok.Data
        public static class MemberData implements java.io.Serializable {
            private String role;
            private String name;
            private String affiliation;
            private String introduction;
            private String profileImageUrl;
            private String snsEmail;
            private String snsLinkedin;
            private String snsGithub;
            private String snsBehance;
        }
    }

}
