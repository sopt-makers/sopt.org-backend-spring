package sopt.org.homepage.application.admin.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.application.admin.dto.AddAdminAboutRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminAboutResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminCommonRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminCommonResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminConfirmRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminConfirmResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminHomeRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminHomeResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminRecruitRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminRecruitResponseDto;
import sopt.org.homepage.application.admin.dto.AddAdminRequestDto;
import sopt.org.homepage.application.admin.dto.AddAdminResponseDto;
import sopt.org.homepage.application.admin.dto.GetAdminRequestDto;
import sopt.org.homepage.application.admin.dto.GetAdminResponseDto;
import sopt.org.homepage.application.admin.dto.response.main.news.AddAdminNewsResponseRecordDto;
import sopt.org.homepage.application.admin.dto.request.main.curriculum.AddAdminPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.introduction.AddAdminPartIntroductionRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.curriculum.AddAdminRecruitPartCurriculumRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.question.AddAdminRecruitQuestionRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.recruit.schedule.AddAdminRecruitScheduleRequestDto;
import sopt.org.homepage.activityschedule.ActivitySchedule;
import sopt.org.homepage.activityschedule.ActivityScheduleService;
import sopt.org.homepage.activityschedule.dto.BulkCreateActivitySchedulesCommand;
import sopt.org.homepage.generation.vo.BrandingColor;
import sopt.org.homepage.application.admin.dto.request.main.activityschedule.AddAdminActivityScheduleRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.review.AddAdminReviewRequestDto;
import sopt.org.homepage.application.admin.dto.response.main.activityschedule.GetAdminActivityScheduleResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.branding.GetAdminBrandingColorResponseRecordDto;
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
import sopt.org.homepage.news.News;
import sopt.org.homepage.news.NewsService;
import sopt.org.homepage.news.dto.BulkCreateNewsCommand;
import sopt.org.homepage.part.PartService;
import sopt.org.homepage.part.dto.BulkCreatePartsCommand;
import sopt.org.homepage.part.dto.PartDetailView;
import sopt.org.homepage.recruitment.RecruitmentService;
import sopt.org.homepage.recruitment.dto.BulkCreateRecruitmentsCommand;
import sopt.org.homepage.recruitment.dto.RecruitmentView;
import sopt.org.homepage.application.admin.dto.response.main.review.GetAdminReviewResponseRecordDto;
import sopt.org.homepage.application.homepage.review.domain.HomepageReview;
import sopt.org.homepage.application.homepage.review.dto.BulkCreateHomepageReviewsCommand;
import sopt.org.homepage.application.homepage.review.service.HomepageReviewService;
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
    private final HomepageReviewService homepageReviewService;
    private final ActivityScheduleService activityScheduleService;

    // ===== Infrastructure Services =====
    private final S3Service s3Service;
    private final CacheService cacheService;
    private final NewsService newsService;

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

        String homeHeaderImageUrl = s3Service.generatePresignedUrl(
                request.getHomeHeaderImageFileName(), baseDir);
        cachedData.setHomeHeaderImageUrl(homeHeaderImageUrl);

        // BrandingColor
        var brandingColorDto = request.getBrandingColor();
        cachedData.setDarkModeKeyColor(brandingColorDto.getDarkModeKeyColor());
        cachedData.setDarkModeTextColor(brandingColorDto.getDarkModeTextColor());
        cachedData.setLightModeKeyColor(brandingColorDto.getLightModeKeyColor());
        cachedData.setLightModeTextColor(brandingColorDto.getLightModeTextColor());

        // ===== 2. CoreValue Presigned URLs =====
        List<CachedAdminData.CoreValueData> coreValueDataList = new ArrayList<>();
        for (var cv : request.getCoreValue()) {
            String imageUrl = s3Service.generatePresignedUrl(
                    cv.getImageFileName(), baseDir + "coreValue/");

            coreValueDataList.add(CachedAdminData.CoreValueData.builder()
                    .value(cv.getValue())
                    .description(cv.getDescription())
                    .detailDescription((cv.getDetailDescription()))
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

        // ===== News Presigned URLs =====
        List<CachedAdminData.NewsData> newsDataList = new ArrayList<>();
        for (var news : request.getNews()) {
            String newsImageUrl = s3Service.generatePresignedUrl(
                    news.getImageFileName(), baseDir + "news/");

            newsDataList.add(CachedAdminData.NewsData.builder()
                    .title(news.getTitle())
                    .link(news.getLink())
                    .imageUrl(newsImageUrl)
                    .build());
        }
        cachedData.setNews(newsDataList);

        // ===== 4. 나머지 데이터 저장 (DTO 그대로 저장) =====
        cachedData.setPartIntroductions(new ArrayList<>(request.getPartIntroduction()));
        cachedData.setPartCurriculums(new ArrayList<>(request.getPartCurriculum()));
        cachedData.setRecruitSchedules(new ArrayList<>(request.getRecruitSchedule()));
        cachedData.setRecruitPartCurriculums(new ArrayList<>(request.getRecruitPartCurriculum()));
        cachedData.setRecruitQuestions(new ArrayList<>(request.getRecruitQuestion()));
        cachedData.setReviews(new ArrayList<>(request.getReview()));
        cachedData.setActivitySchedules(new ArrayList<>(request.getActivitySchedule()));

        // ===== 5. 캐시에 저장 =====
        cacheService.put(CacheType.ADMIN_MAIN_DATA, String.valueOf(generationId), cachedData);

        log.info("Admin main data cached for generation: {}", generationId);

        // ===== 6. Response 생성 =====
        return AddAdminResponseDto.builder()
                .generation(generationId)
                .headerImage(headerImageUrl)
                .homeHeaderImage(homeHeaderImageUrl)
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
        String homeHeaderImageUrl = s3Service.getOriginalUrl(cachedData.getHomeHeaderImageUrl());

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
                        .homeHeaderImage(homeHeaderImageUrl)
                        .brandingColor(CreateGenerationCommand.BrandingColorCommand.builder()
                                .darkModeKeyColor(stripHash(cachedData.getDarkModeKeyColor()))
                                .darkModeTextColor(cachedData.getDarkModeTextColor())
                                .lightModeKeyColor(stripHash(cachedData.getLightModeKeyColor()))
                                .lightModeTextColor(cachedData.getLightModeTextColor())
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
                    .detailDescription(cv.getDetailDescription())
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

        // ===== 10. News 일괄 생성 =====
        newsService.bulkCreate(
                BulkCreateNewsCommand.builder()
                        .news(cachedData.getNews().stream()
                                .map(n -> BulkCreateNewsCommand.NewsData.builder()
                                        .title(n.getTitle())
                                        .link(n.getLink())
                                        .imageUrl(s3Service.getOriginalUrl(n.getImageUrl()))
                                        .build())
                                .toList())
                        .build()
        );

        // ===== 11. Review 일괄 생성 =====
        homepageReviewService.bulkCreate(
                BulkCreateHomepageReviewsCommand.builder()
                        .reviews(cachedData.getReviews().stream()
                                .map(r -> BulkCreateHomepageReviewsCommand.ReviewData.builder()
                                        .title(r.getTitle())
                                        .content(r.getContent())
                                        .authorInfo(r.getAuthorInfo())
                                        .build())
                                .toList())
                        .build()
        );

        // ===== 12. ActivitySchedule 일괄 생성 =====
        activityScheduleService.bulkCreate(
                BulkCreateActivitySchedulesCommand.builder()
                        .generationId(generationId)
                        .activitySchedules(cachedData.getActivitySchedules().stream()
                                .map(s -> BulkCreateActivitySchedulesCommand.ActivityScheduleData.builder()
                                        .name(s.getName())
                                        .startDate(java.time.LocalDate.parse(s.getStartDate()))
                                        .endDate(s.getEndDate() != null ? java.time.LocalDate.parse(s.getEndDate()) : null)
                                        .build())
                                .toList())
                        .build()
        );

        // ===== 13. 캐시 삭제 =====
        cacheService.evict(CacheType.ADMIN_MAIN_DATA, String.valueOf(generationId));


        log.info("Admin main data confirmed for generation: {}", generationId);

        return AddAdminConfirmResponseDto.builder()
                .message("파일 업로드 확인 및 어드민 데이터 배포 성공")
                .build();
    }

    // =========================================================
    // 탭별 배포 - 공통 탭
    // =========================================================

    @Override
    @Transactional
    public AddAdminCommonResponseDto addCommonData(AddAdminCommonRequestDto request) {
        log.info("공통 탭 배포 1단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();

        CachedCommonData cachedData = new CachedCommonData();
        cachedData.setGenerationId(generationId);
        cachedData.setName(request.getName());

        var bc = request.getBrandingColor();
        BrandingColor.builder()
                .darkModeKeyColor(stripHash(bc.getDarkModeKeyColor()))
                .darkModeTextColor(bc.getDarkModeTextColor())
                .lightModeKeyColor(stripHash(bc.getLightModeKeyColor()))
                .lightModeTextColor(bc.getLightModeTextColor())
                .build();

        cachedData.setDarkModeKeyColor(bc.getDarkModeKeyColor());
        cachedData.setDarkModeTextColor(bc.getDarkModeTextColor());
        cachedData.setLightModeKeyColor(bc.getLightModeKeyColor());
        cachedData.setLightModeTextColor(bc.getLightModeTextColor());

        if (request.getRecruitSchedule() != null) {
            cachedData.setRecruitSchedules(new ArrayList<>(request.getRecruitSchedule()));
        }

        cacheService.put(CacheType.ADMIN_MAIN_DATA, "common_" + generationId, cachedData);
        log.info("공통 탭 캐시 저장 완료 - generation={}", generationId);

        return AddAdminCommonResponseDto.builder()
                .generation(generationId)
                .build();
    }

    @Override
    @Transactional
    public AddAdminConfirmResponseDto addCommonDataConfirm(AddAdminConfirmRequestDto request) {
        log.info("공통 탭 배포 2단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();

        CachedCommonData cachedData = cacheService.get(
                CacheType.ADMIN_MAIN_DATA, "common_" + generationId, CachedCommonData.class);

        if (cachedData == null) {
            throw new ClientBadRequestException(
                    "공통 탭 캐시 데이터 없음. 배포 1단계를 먼저 호출하세요. generation=" + generationId);
        }

        // BrandingColor: keyColor VO는 # 없는 6자리 hex 요구 → # 제거 후 전달
        BrandingColor brandingColor = BrandingColor.builder()
                .darkModeKeyColor(stripHash(cachedData.getDarkModeKeyColor()))
                .darkModeTextColor(cachedData.getDarkModeTextColor())
                .lightModeKeyColor(stripHash(cachedData.getLightModeKeyColor()))
                .lightModeTextColor(cachedData.getLightModeTextColor())
                .build();

        generationService.createOrUpdateCommon(generationId, cachedData.getName(), brandingColor);

        if (cachedData.getRecruitSchedules() != null) {
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
        }

        cacheService.evict(CacheType.ADMIN_MAIN_DATA, "common_" + generationId);
        log.info("공통 탭 배포 완료 - generation={}", generationId);

        return AddAdminConfirmResponseDto.builder()
                .message("공통 탭 배포 성공")
                .build();
    }

    // =========================================================
    // 탭별 배포 - 홈 탭
    // =========================================================

    @Override
    @Transactional
    public AddAdminHomeResponseDto addHomeData(AddAdminHomeRequestDto request) {
        log.info("홈 탭 배포 1단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();
        String baseDir = generationId + "/";

        CachedHomeData cachedData = new CachedHomeData();
        cachedData.setGenerationId(generationId);

        String homeHeaderImageUrl = StringUtils.hasText(request.getHomeHeaderImageFileName())
                ? s3Service.generatePresignedUrl(request.getHomeHeaderImageFileName(), baseDir)
                : null;
        cachedData.setHomeHeaderImageUrl(homeHeaderImageUrl);

        List<CachedAdminData.NewsData> newsDataList = null;
        if (request.getNews() != null) {
            newsDataList = new ArrayList<>();
            for (var news : request.getNews()) {
                String newsImageUrl = s3Service.generatePresignedUrl(
                        news.getImageFileName(), baseDir + "news/");
                newsDataList.add(CachedAdminData.NewsData.builder()
                        .title(news.getTitle())
                        .link(news.getLink())
                        .imageUrl(newsImageUrl)
                        .build());
            }
            cachedData.setNews(newsDataList);
        }

        if (request.getReview() != null) {
            cachedData.setReviews(new ArrayList<>(request.getReview()));
        }

        cacheService.put(CacheType.ADMIN_MAIN_DATA, "home_" + generationId, cachedData);
        log.info("홈 탭 캐시 저장 완료 - generation={}", generationId);

        return AddAdminHomeResponseDto.builder()
                .generation(generationId)
                .homeHeaderImage(homeHeaderImageUrl)
                .news(newsDataList != null
                        ? newsDataList.stream()
                                .map(n -> AddAdminNewsResponseRecordDto.builder()
                                        .title(n.getTitle())
                                        .imagePresignedUrl(n.getImageUrl())
                                        .build())
                                .toList()
                        : null)
                .build();
    }

    @Override
    @Transactional
    public AddAdminConfirmResponseDto addHomeDataConfirm(AddAdminConfirmRequestDto request) {
        log.info("홈 탭 배포 2단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();

        CachedHomeData cachedData = cacheService.get(
                CacheType.ADMIN_MAIN_DATA, "home_" + generationId, CachedHomeData.class);

        if (cachedData == null) {
            throw new ClientBadRequestException(
                    "홈 탭 캐시 데이터 없음. 배포 1단계를 먼저 호출하세요. generation=" + generationId);
        }

        if (cachedData.getHomeHeaderImageUrl() != null) {
            String homeHeaderImageUrl = s3Service.getOriginalUrl(cachedData.getHomeHeaderImageUrl());
            generationService.updateHomeHeaderImage(generationId, homeHeaderImageUrl);
        }

        if (cachedData.getNews() != null) {
            newsService.bulkCreate(
                    BulkCreateNewsCommand.builder()
                            .news(cachedData.getNews().stream()
                                    .map(n -> BulkCreateNewsCommand.NewsData.builder()
                                            .title(n.getTitle())
                                            .link(n.getLink())
                                            .imageUrl(s3Service.getOriginalUrl(n.getImageUrl()))
                                            .build())
                                    .toList())
                            .build()
            );
        }

        if (cachedData.getReviews() != null) {
            homepageReviewService.bulkCreate(
                    BulkCreateHomepageReviewsCommand.builder()
                            .reviews(cachedData.getReviews().stream()
                                    .map(r -> BulkCreateHomepageReviewsCommand.ReviewData.builder()
                                            .title(r.getTitle())
                                            .content(r.getContent())
                                            .authorInfo(r.getAuthorInfo())
                                            .build())
                                    .toList())
                            .build()
            );
        }

        cacheService.evict(CacheType.ADMIN_MAIN_DATA, "home_" + generationId);
        log.info("홈 탭 배포 완료 - generation={}", generationId);

        return AddAdminConfirmResponseDto.builder()
                .message("홈 탭 배포 성공")
                .build();
    }

    // =========================================================
    // 탭별 배포 - 소개 탭
    // =========================================================

    @Override
    @Transactional
    public AddAdminAboutResponseDto addAboutData(AddAdminAboutRequestDto request) {
        log.info("소개 탭 배포 1단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();
        String baseDir = generationId + "/";

        CachedAboutData cachedData = new CachedAboutData();
        cachedData.setGenerationId(generationId);

        String headerImageUrl = StringUtils.hasText(request.getHeaderImageFileName())
                ? s3Service.generatePresignedUrl(request.getHeaderImageFileName(), baseDir)
                : null;
        cachedData.setHeaderImageUrl(headerImageUrl);

        List<CachedAdminData.CoreValueData> coreValueDataList = null;
        if (request.getCoreValue() != null) {
            coreValueDataList = new ArrayList<>();
            for (var cv : request.getCoreValue()) {
                String imageUrl = StringUtils.hasText(cv.getImageFileName())
                        ? s3Service.generatePresignedUrl(cv.getImageFileName(), baseDir + "coreValue/")
                        : null;
                coreValueDataList.add(CachedAdminData.CoreValueData.builder()
                        .value(cv.getValue())
                        .description(cv.getDescription())
                        .detailDescription(cv.getDetailDescription())
                        .imageUrl(imageUrl)
                        .build());
            }
            cachedData.setCoreValues(coreValueDataList);
        }

        List<CachedAdminData.MemberData> memberDataList = null;
        if (request.getMember() != null) {
            memberDataList = new ArrayList<>();
            for (var member : request.getMember()) {
                String profileImageUrl = StringUtils.hasText(member.getProfileImageFileName())
                        ? s3Service.generatePresignedUrl(member.getProfileImageFileName(), baseDir + "member/")
                        : null;
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
        }

        if (request.getActivitySchedule() != null) {
            cachedData.setActivitySchedules(new ArrayList<>(request.getActivitySchedule()));
        }

        cacheService.put(CacheType.ADMIN_MAIN_DATA, "about_" + generationId, cachedData);
        log.info("소개 탭 캐시 저장 완료 - generation={}", generationId);

        return AddAdminAboutResponseDto.builder()
                .generation(generationId)
                .headerImage(headerImageUrl)
                .coreValues(coreValueDataList != null
                        ? coreValueDataList.stream()
                                .map(cv -> AddAdminCoreValueResponseRecordDto.builder()
                                        .value(cv.getValue())
                                        .image(cv.getImageUrl())
                                        .build())
                                .toList()
                        : null)
                .members(memberDataList != null
                        ? memberDataList.stream()
                                .map(m -> AddAdminMemberResponseRecordDto.builder()
                                        .role(m.getRole())
                                        .name(m.getName())
                                        .profileImage(m.getProfileImageUrl())
                                        .build())
                                .toList()
                        : null)
                .build();
    }

    @Override
    @Transactional
    public AddAdminConfirmResponseDto addAboutDataConfirm(AddAdminConfirmRequestDto request) {
        log.info("소개 탭 배포 2단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();

        CachedAboutData cachedData = cacheService.get(
                CacheType.ADMIN_MAIN_DATA, "about_" + generationId, CachedAboutData.class);

        if (cachedData == null) {
            throw new ClientBadRequestException(
                    "소개 탭 캐시 데이터 없음. 배포 1단계를 먼저 호출하세요. generation=" + generationId);
        }

        if (cachedData.getHeaderImageUrl() != null) {
            String headerImageUrl = s3Service.getOriginalUrl(cachedData.getHeaderImageUrl());
            generationService.updateHeaderImage(generationId, headerImageUrl);
        }

        if (cachedData.getCoreValues() != null) {
            boolean hasNullCvImage = cachedData.getCoreValues().stream()
                    .anyMatch(cv -> cv.getImageUrl() == null);
            Map<String, String> existingCvImageByValue = hasNullCvImage
                    ? coreValueService.findByGeneration(generationId).stream()
                            .collect(Collectors.toMap(CoreValueView::value, CoreValueView::imageUrl))
                    : null;

            List<BulkCreateCoreValuesCommand.CoreValueData> coreValueDataList = new ArrayList<>();
            for (int i = 0; i < cachedData.getCoreValues().size(); i++) {
                var cv = cachedData.getCoreValues().get(i);
                String imageUrl = cv.getImageUrl() != null
                        ? s3Service.getOriginalUrl(cv.getImageUrl())
                        : (existingCvImageByValue != null ? existingCvImageByValue.get(cv.getValue()) : null);
                coreValueDataList.add(BulkCreateCoreValuesCommand.CoreValueData.builder()
                        .value(cv.getValue())
                        .description(cv.getDescription())
                        .detailDescription(cv.getDetailDescription())
                        .imageUrl(imageUrl)
                        .displayOrder(i)
                        .build());
            }
            coreValueService.bulkCreate(
                    BulkCreateCoreValuesCommand.builder()
                            .generationId(generationId)
                            .coreValues(coreValueDataList)
                            .build()
            );
        }

        if (cachedData.getMembers() != null) {
            boolean hasNullMemberImage = cachedData.getMembers().stream()
                    .anyMatch(m -> m.getProfileImageUrl() == null);
            Map<String, String> existingMemberImageByName = hasNullMemberImage
                    ? memberService.findByGeneration(generationId).stream()
                            .collect(Collectors.toMap(MemberDetailView::name, MemberDetailView::profileImageUrl))
                    : null;

            List<BulkCreateMembersCommand.MemberData> memberDataList = new ArrayList<>();
            for (int i = 0; i < cachedData.getMembers().size(); i++) {
                var m = cachedData.getMembers().get(i);
                String profileImageUrl = m.getProfileImageUrl() != null
                        ? s3Service.getOriginalUrl(m.getProfileImageUrl())
                        : (existingMemberImageByName != null ? existingMemberImageByName.get(m.getName()) : null);
                memberDataList.add(BulkCreateMembersCommand.MemberData.builder()
                        .role(m.getRole())
                        .name(m.getName())
                        .affiliation(m.getAffiliation())
                        .introduction(m.getIntroduction())
                        .profileImageUrl(profileImageUrl)
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
        }

        if (cachedData.getActivitySchedules() != null) {
            activityScheduleService.bulkCreate(
                    BulkCreateActivitySchedulesCommand.builder()
                            .generationId(generationId)
                            .activitySchedules(cachedData.getActivitySchedules().stream()
                                    .map(s -> BulkCreateActivitySchedulesCommand.ActivityScheduleData.builder()
                                            .name(s.getName())
                                            .startDate(java.time.LocalDate.parse(s.getStartDate()))
                                            .endDate(s.getEndDate() != null ? java.time.LocalDate.parse(s.getEndDate()) : null)
                                            .build())
                                    .toList())
                            .build()
            );
        }

        cacheService.evict(CacheType.ADMIN_MAIN_DATA, "about_" + generationId);
        log.info("소개 탭 배포 완료 - generation={}", generationId);

        return AddAdminConfirmResponseDto.builder()
                .message("소개 탭 배포 성공")
                .build();
    }

    // =========================================================
    // 탭별 배포 - 모집안내 탭
    // =========================================================

    @Override
    @Transactional
    public AddAdminRecruitResponseDto addRecruitData(AddAdminRecruitRequestDto request) {
        log.info("모집안내 탭 배포 1단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();
        String baseDir = generationId + "/";

        CachedRecruitData cachedData = new CachedRecruitData();
        cachedData.setGenerationId(generationId);

        String recruitHeaderImageUrl = StringUtils.hasText(request.getRecruitHeaderImageFileName())
                ? s3Service.generatePresignedUrl(request.getRecruitHeaderImageFileName(), baseDir)
                : null;
        cachedData.setRecruitHeaderImageUrl(recruitHeaderImageUrl);

        if (request.getPartIntroduction() != null) {
            cachedData.setPartIntroductions(new ArrayList<>(request.getPartIntroduction()));
        }
        if (request.getPartCurriculum() != null) {
            cachedData.setPartCurriculums(new ArrayList<>(request.getPartCurriculum()));
        }
        if (request.getRecruitPartCurriculum() != null) {
            cachedData.setRecruitPartCurriculums(new ArrayList<>(request.getRecruitPartCurriculum()));
        }
        if (request.getRecruitQuestion() != null) {
            cachedData.setRecruitQuestions(new ArrayList<>(request.getRecruitQuestion()));
        }

        cacheService.put(CacheType.ADMIN_MAIN_DATA, "recruit_" + generationId, cachedData);
        log.info("모집안내 탭 캐시 저장 완료 - generation={}", generationId);

        return AddAdminRecruitResponseDto.builder()
                .generation(generationId)
                .recruitHeaderImage(recruitHeaderImageUrl)
                .build();
    }

    @Override
    @Transactional
    public AddAdminConfirmResponseDto addRecruitDataConfirm(AddAdminConfirmRequestDto request) {
        log.info("모집안내 탭 배포 2단계 - generation={}", request.getGeneration());

        Integer generationId = request.getGeneration();

        CachedRecruitData cachedData = cacheService.get(
                CacheType.ADMIN_MAIN_DATA, "recruit_" + generationId, CachedRecruitData.class);

        if (cachedData == null) {
            throw new ClientBadRequestException(
                    "모집안내 탭 캐시 데이터 없음. 배포 1단계를 먼저 호출하세요. generation=" + generationId);
        }

        if (cachedData.getRecruitHeaderImageUrl() != null) {
            String recruitHeaderImageUrl = s3Service.getOriginalUrl(cachedData.getRecruitHeaderImageUrl());
            generationService.updateRecruitHeaderImage(generationId, recruitHeaderImageUrl);
        }

        if (cachedData.getPartIntroductions() != null || cachedData.getPartCurriculums() != null) {
            partService.bulkCreate(
                    BulkCreatePartsCommand.builder()
                            .generationId(generationId)
                            .partIntroductions(cachedData.getPartIntroductions() != null
                                    ? cachedData.getPartIntroductions().stream()
                                            .map(pi -> BulkCreatePartsCommand.PartData.builder()
                                                    .part(pi.getPart())
                                                    .description(pi.getDescription())
                                                    .build())
                                            .toList()
                                    : List.of())
                            .partCurriculums(cachedData.getPartCurriculums() != null
                                    ? cachedData.getPartCurriculums().stream()
                                            .map(pc -> BulkCreatePartsCommand.PartCurriculumData.builder()
                                                    .part(pc.getPart())
                                                    .curriculums(pc.getCurriculums())
                                                    .build())
                                            .toList()
                                    : List.of())
                            .build()
            );
        }

        if (cachedData.getRecruitPartCurriculums() != null) {
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
        }

        if (cachedData.getRecruitQuestions() != null) {
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
        }

        cacheService.evict(CacheType.ADMIN_MAIN_DATA, "recruit_" + generationId);
        log.info("모집안내 탭 배포 완료 - generation={}", generationId);

        return AddAdminConfirmResponseDto.builder()
                .message("모집안내 탭 배포 성공")
                .build();
    }

    // =========================================================
    // 유틸리티
    // =========================================================

    /** BrandingColor VO는 '#' 없는 6자리 hex 요구 → '#' 제거 */
    private String stripHash(String hexColor) {
        if (hexColor != null && hexColor.startsWith("#")) {
            return hexColor.substring(1);
        }
        return hexColor;
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
        List<News> newsEntities = newsService.findAll();
        List<HomepageReview> reviews = homepageReviewService.findAll();
        List<ActivitySchedule> activitySchedules = activityScheduleService.findByGeneration(generationId);

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
                        .darkModeKeyColor(generation.brandingColor().darkModeKeyColor())
                        .darkModeTextColor(generation.brandingColor().darkModeTextColor())
                        .lightModeKeyColor(generation.brandingColor().lightModeKeyColor())
                        .lightModeTextColor(generation.brandingColor().lightModeTextColor())
                        .build())
                .partIntroduction(parts.stream()
                        .map(p -> GetAdminPartIntroductionResponseRecordDto.builder()
                                .part(p.part())
                                .description(p.description())
                                .build())
                        .toList())
                .latestNews(newsEntities.stream()
                        .map(news -> GetAdminLatestNewsResponseRecordDto.builder()
                                .id(news.getId())
                                .title(news.getTitle())
                                .build())
                        .toList())
                .headerImage(generation.headerImage())
                .homeHeaderImage(generation.homeHeaderImage())
                .coreValue(coreValues.stream()
                        .map(cv -> GetAdminCoreValueResponseRecordDto.builder()
                                .value(cv.value())
                                .description(cv.description())
                                .detailDescription(cv.detailDescription())
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
                .review(reviews.stream()
                        .map(r -> GetAdminReviewResponseRecordDto.builder()
                                .id(r.getId())
                                .title(r.getTitle())
                                .content(r.getContent())
                                .authorInfo(r.getAuthorInfo())
                                .build())
                        .toList())
                .activitySchedule(activitySchedules.stream()
                        .map(s -> GetAdminActivityScheduleResponseRecordDto.builder()
                                .name(s.getName())
                                .startDate(s.getStartDate().toString())
                                .endDate(s.getEndDate() != null ? s.getEndDate().toString() : null)
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
        private String homeHeaderImageUrl;

        // BrandingColor
        private String darkModeKeyColor;
        private String darkModeTextColor;
        private String lightModeKeyColor;
        private String lightModeTextColor;

        // CoreValue
        private List<CoreValueData> coreValues;

        // Review
        private List<AddAdminReviewRequestDto> reviews;

        // News
        private List<NewsData> news;

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

        // ActivitySchedule
        private List<AddAdminActivityScheduleRequestDto> activitySchedules;

        @lombok.Builder
        @lombok.Data
        public static class CoreValueData implements java.io.Serializable {
            private String value;
            private String description;
            private String detailDescription;
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

        @lombok.Builder
        @lombok.Data
        public static class NewsData implements java.io.Serializable {
            private String title;
            private String link;
            private String imageUrl;
        }
    }

    // ===== 탭별 캐시 데이터 클래스 =====

    @lombok.Data
    public static class CachedCommonData implements java.io.Serializable {
        private Integer generationId;
        private String name;
        private String darkModeKeyColor;
        private String darkModeTextColor;
        private String lightModeKeyColor;
        private String lightModeTextColor;
        private List<AddAdminRecruitScheduleRequestDto> recruitSchedules;
    }

    @lombok.Data
    public static class CachedHomeData implements java.io.Serializable {
        private Integer generationId;
        private String homeHeaderImageUrl;
        private List<CachedAdminData.NewsData> news;
        private List<AddAdminReviewRequestDto> reviews;
    }

    @lombok.Data
    public static class CachedAboutData implements java.io.Serializable {
        private Integer generationId;
        private String headerImageUrl;
        private List<CachedAdminData.CoreValueData> coreValues;
        private List<CachedAdminData.MemberData> members;
        private List<AddAdminActivityScheduleRequestDto> activitySchedules;
    }

    @lombok.Data
    public static class CachedRecruitData implements java.io.Serializable {
        private Integer generationId;
        private String recruitHeaderImageUrl;
        private List<AddAdminPartIntroductionRequestDto> partIntroductions;
        private List<AddAdminPartCurriculumRequestDto> partCurriculums;
        private List<AddAdminRecruitPartCurriculumRequestDto> recruitPartCurriculums;
        private List<AddAdminRecruitQuestionRequestDto> recruitQuestions;
    }

}
