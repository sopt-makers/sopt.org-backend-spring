package sopt.org.homepage.admin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.admin.dto.request.main.AddAdminConfirmRequestDto;
import sopt.org.homepage.admin.dto.request.main.AddAdminRequestDto;
import sopt.org.homepage.admin.dto.request.main.GetAdminRequestDto;
import sopt.org.homepage.admin.dto.request.news.AddAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.DeleteAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.request.news.GetAdminNewsRequestDto;
import sopt.org.homepage.admin.dto.response.main.AddAdminConfirmResponseDto;
import sopt.org.homepage.admin.dto.response.main.AddAdminResponseDto;
import sopt.org.homepage.admin.dto.response.main.GetAdminResponseDto;
import sopt.org.homepage.admin.dto.response.main.core.AddAdminCoreValueResponseRecordDto;
import sopt.org.homepage.admin.dto.response.main.member.AddAdminMemberResponseRecordDto;
import sopt.org.homepage.admin.dto.response.news.AddAdminNewsResponseDto;
import sopt.org.homepage.admin.dto.response.news.DeleteAdminNewsResponseDto;
import sopt.org.homepage.admin.dto.response.news.GetAdminNewsResponseDto;
import sopt.org.homepage.aws.s3.S3Service;
import sopt.org.homepage.cache.CacheService;
import sopt.org.homepage.common.constants.CacheType;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.corevalue.service.command.CoreValueCommandService;
import sopt.org.homepage.corevalue.service.command.dto.BulkCreateCoreValuesCommand;
import sopt.org.homepage.corevalue.service.query.CoreValueQueryService;
import sopt.org.homepage.corevalue.service.query.dto.CoreValueView;
import sopt.org.homepage.faq.service.command.FAQCommandService;
import sopt.org.homepage.faq.service.command.dto.BulkCreateFAQsCommand;
import sopt.org.homepage.faq.service.query.FAQQueryService;
import sopt.org.homepage.faq.service.query.dto.FAQView;
import sopt.org.homepage.generation.service.command.GenerationCommandService;
import sopt.org.homepage.generation.service.command.dto.CreateGenerationCommand;
import sopt.org.homepage.generation.service.query.GenerationQueryService;
import sopt.org.homepage.generation.service.query.dto.GenerationDetailView;
import sopt.org.homepage.main.entity.MainNewsEntity;
import sopt.org.homepage.main.repository.MainNewsRepository;
import sopt.org.homepage.member.service.command.MemberCommandService;
import sopt.org.homepage.member.service.command.dto.BulkCreateMembersCommand;
import sopt.org.homepage.member.service.query.MemberQueryService;
import sopt.org.homepage.member.service.query.dto.MemberDetailView;
import sopt.org.homepage.part.service.command.PartCommandService;
import sopt.org.homepage.part.service.command.dto.BulkCreatePartsCommand;
import sopt.org.homepage.part.service.query.PartQueryService;
import sopt.org.homepage.part.service.query.dto.PartDetailView;
import sopt.org.homepage.recruitment.service.command.RecruitPartIntroductionCommandService;
import sopt.org.homepage.recruitment.service.command.RecruitmentCommandService;
import sopt.org.homepage.recruitment.service.command.dto.BulkCreateRecruitPartIntroductionsCommand;
import sopt.org.homepage.recruitment.service.command.dto.BulkCreateRecruitmentsCommand;
import sopt.org.homepage.recruitment.service.query.RecruitPartIntroductionQueryService;
import sopt.org.homepage.recruitment.service.query.RecruitmentQueryService;
import sopt.org.homepage.recruitment.service.query.dto.RecruitPartIntroductionView;
import sopt.org.homepage.recruitment.service.query.dto.RecruitmentView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * AdminServiceImpl
 *
 * 책임:
 * - Admin API 처리
 * - 여러 도메인의 Command Service 조합
 * - S3 Presigned URL 생성 및 캐시 관리
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    // Domain Command Services
    private final GenerationCommandService generationCommandService;
    private final CoreValueCommandService coreValueCommandService;
    private final MemberCommandService memberCommandService;
    private final PartCommandService partCommandService;
    private final RecruitmentCommandService recruitmentCommandService;
    private final RecruitPartIntroductionCommandService recruitPartIntroductionCommandService;
    private final FAQCommandService faqCommandService;

    // Domain Query Services
    private final GenerationQueryService generationQueryService;
    private final CoreValueQueryService coreValueQueryService;
    private final MemberQueryService memberQueryService;
    private final PartQueryService partQueryService;
    private final RecruitmentQueryService recruitmentQueryService;
    private final RecruitPartIntroductionQueryService recruitPartIntroductionQueryService;
    private final FAQQueryService faqQueryService;

    // Infrastructure Services
    private final S3Service s3Service;
    private final CacheService cacheService;
    private final MainNewsRepository mainNewsRepository;

    /**
     * Admin 메인 데이터 배포
     *
     * 1단계: Presigned URL 생성 및 캐시 저장
     */
    @Override
    @Transactional
    public AddAdminResponseDto addMainData(AddAdminRequestDto request) {
        log.info("Adding admin main data for generation: {}", request.getGeneration());

        Integer generationId = request.getGeneration();
        String baseDir = generationId + "/";

        // 1. Generation 데이터 준비 (캐시에만 저장)
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
        cachedData.setBrandingColor(CachedAdminData.BrandingColorData.builder()
                .main(request.getBrandingColor().getMain())
                .low(request.getBrandingColor().getLow())
                .high(request.getBrandingColor().getHigh())
                .point(request.getBrandingColor().getPoint())
                .build());

        // MainButton
        cachedData.setMainButton(CachedAdminData.MainButtonData.builder()
                .text(request.getMainButton().getText())
                .keyColor(request.getMainButton().getKeyColor())
                .subColor(request.getMainButton().getSubColor())
                .build());

        // 2. CoreValue Presigned URLs
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

        // 3. Member Presigned URLs
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
                    .sns(member.getSns() != null ? CachedAdminData.SnsLinksData.builder()
                            .email(member.getSns().getEmail())
                            .linkedin(member.getSns().getLinkedin())
                            .github(member.getSns().getGithub())
                            .behance(member.getSns().getBehance())
                            .build() : null)
                    .build());
        }
        cachedData.setMembers(memberDataList);

        // 4. 나머지 데이터 저장
        cachedData.setPartIntroductions(request.getPartIntroduction().stream()
                .map(pi -> CachedAdminData.PartIntroductionData.builder()
                        .part(pi.getPart())
                        .description(pi.getDescription())
                        .build())
                .toList());

        cachedData.setPartCurriculums(request.getPartCurriculum().stream()
                .map(pc -> CachedAdminData.PartCurriculumData.builder()
                        .part(pc.getPart())
                        .curriculums(pc.getCurriculums())
                        .build())
                .toList());

        cachedData.setRecruitSchedules(request.getRecruitSchedule().stream()
                .map(rs -> CachedAdminData.RecruitScheduleData.builder()
                        .type(rs.getType())
                        .schedule(CachedAdminData.ScheduleData.builder()
                                .applicationStartTime(rs.getSchedule().getApplicationStartTime())
                                .applicationEndTime(rs.getSchedule().getApplicationEndTime())
                                .applicationResultTime(rs.getSchedule().getApplicationResultTime())
                                .interviewStartTime(rs.getSchedule().getInterviewStartTime())
                                .interviewEndTime(rs.getSchedule().getInterviewEndTime())
                                .finalResultTime(rs.getSchedule().getFinalResultTime())
                                .build())
                        .build())
                .toList());

        cachedData.setRecruitPartCurriculums(request.getRecruitPartCurriculum().stream()
                .map(rpc -> CachedAdminData.RecruitPartCurriculumData.builder()
                        .part(rpc.getPart())
                        .introduction(CachedAdminData.IntroductionData.builder()
                                .content(rpc.getIntroduction().getContent())
                                .preference(rpc.getIntroduction().getPreference())
                                .build())
                        .build())
                .toList());

        cachedData.setRecruitQuestions(request.getRecruitQuestion().stream()
                .map(rq -> CachedAdminData.RecruitQuestionData.builder()
                        .part(rq.getPart())
                        .question(rq.getQuestions().stream()
                                .map(q -> CachedAdminData.QuestionData.builder()
                                        .question(q.getQuestion())
                                        .answer(q.getAnswer())
                                        .build())
                                .toList())
                        .build())
                .toList());

        // 5. 캐시에 저장
        cacheService.put(CacheType.ADMIN_MAIN_DATA, String.valueOf(generationId), cachedData);

        log.info("Admin main data cached for generation: {}", generationId);

        // 6. Response 생성 (Presigned URL 반환)
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
     * Admin 메인 데이터 배포 확인
     *
     * 2단계: 캐시에서 데이터 읽어서 실제 DB에 저장
     */
    @Override
    @Transactional
    public AddAdminConfirmResponseDto addMainDataConfirm(AddAdminConfirmRequestDto request) {
        log.info("Confirming admin main data for generation: {}", request.getGeneration());

        Integer generationId = request.getGeneration();

        // 1. 캐시에서 데이터 조회
        CachedAdminData cachedData = cacheService.get(
                CacheType.ADMIN_MAIN_DATA, String.valueOf(generationId), CachedAdminData.class);

        if (cachedData == null) {
            throw new IllegalArgumentException(
                    "Cached admin data not found for generation: " + generationId);
        }

        // 2. Presigned URL → S3 Original URL 변환
        String headerImageUrl = s3Service.getOriginalUrl(cachedData.getHeaderImageUrl());
        String recruitHeaderImageUrl = s3Service.getOriginalUrl(cachedData.getRecruitHeaderImageUrl());

        List<String> coreValueImageUrls = cachedData.getCoreValues().stream()
                .map(cv -> s3Service.getOriginalUrl(cv.getImageUrl()))
                .toList();

        List<String> memberProfileImageUrls = cachedData.getMembers().stream()
                .map(m -> s3Service.getOriginalUrl(m.getProfileImageUrl()))
                .toList();

        // 3. Generation 생성
        generationCommandService.createGeneration(
                CreateGenerationCommand.builder()
                        .id(generationId)
                        .name(cachedData.getName())
                        .headerImage(headerImageUrl)
                        .recruitHeaderImage(recruitHeaderImageUrl)
                        .brandingColor(CreateGenerationCommand.BrandingColorCommand.builder()
                                .main(cachedData.getBrandingColor().getMain())
                                .sub(cachedData.getBrandingColor().getLow())
                                .point(cachedData.getBrandingColor().getHigh())
                                .background(cachedData.getBrandingColor().getPoint())
                                .build())
                        .mainButton(CreateGenerationCommand.MainButtonCommand.builder()
                                .text(cachedData.getMainButton().getText())
                                .keyColor(cachedData.getMainButton().getKeyColor())
                                .subColor(cachedData.getMainButton().getSubColor())
                                .build())
                        .build()
        );

        // 4. CoreValue 일괄 생성
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
        coreValueCommandService.bulkCreateCoreValues(
                BulkCreateCoreValuesCommand.builder()
                        .generationId(generationId)
                        .coreValues(coreValueDataList)
                        .build()
        );

        // 5. Member 일괄 생성
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
                            .email(m.getSns() != null ? m.getSns().getEmail() : null)
                            .linkedin(m.getSns() != null ? m.getSns().getLinkedin() : null)
                            .github(m.getSns() != null ? m.getSns().getGithub() : null)
                            .behance(m.getSns() != null ? m.getSns().getBehance() : null)
                            .build())
                    .build());
        }
        memberCommandService.bulkCreateMembers(
                BulkCreateMembersCommand.builder()
                        .generationId(generationId)
                        .members(memberDataList)
                        .build()
        );

        // 6. Part 일괄 생성
        partCommandService.bulkCreateParts(
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

        // 7. Recruitment 일괄 생성
        recruitmentCommandService.bulkCreateRecruitments(
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

        // 8. RecruitPartIntroduction 일괄 생성
        recruitPartIntroductionCommandService.bulkCreateRecruitPartIntroductions(
                BulkCreateRecruitPartIntroductionsCommand.builder()
                        .generationId(generationId)
                        .partIntroductions(cachedData.getRecruitPartCurriculums().stream()
                                .map(rpc -> BulkCreateRecruitPartIntroductionsCommand.PartIntroductionData.builder()
                                        .part(rpc.getPart())
                                        .introduction(BulkCreateRecruitPartIntroductionsCommand.IntroductionData.builder()
                                                .content(rpc.getIntroduction().getContent())
                                                .preference(rpc.getIntroduction().getPreference())
                                                .build())
                                        .build())
                                .toList())
                        .build()
        );

        // 9. FAQ 일괄 생성
        faqCommandService.bulkCreateFAQs(
                BulkCreateFAQsCommand.builder()
                        .faqs(cachedData.getRecruitQuestions().stream()
                                .map(rq -> BulkCreateFAQsCommand.FAQData.builder()
                                        .part(rq.getPart())
                                        .question(rq.getQuestion().stream()
                                                .map(q -> BulkCreateFAQsCommand.QuestionData.builder()
                                                        .question(q.getQuestion())
                                                        .answer(q.getAnswer())
                                                        .build())
                                                .toList())
                                        .build())
                                .toList())
                        .build()
        );

        // 10. 캐시 삭제
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

        // 각 도메인에서 데이터 조회
        GenerationDetailView generation = generationQueryService.getGenerationDetail(generationId);
        List<CoreValueView> coreValues = coreValueQueryService.getCoreValuesByGeneration(generationId);
        List<MemberDetailView> members = memberQueryService.getMembersByGeneration(generationId);
        List<PartDetailView> parts = partQueryService.getPartsByGeneration(generationId);
        List<RecruitmentView> recruitments = recruitmentQueryService.getRecruitmentsByGeneration(generationId);
        List<RecruitPartIntroductionView> recruitPartIntros =
                recruitPartIntroductionQueryService.getRecruitPartIntroductionsByGeneration(generationId);
        List<FAQView> faqs = faqQueryService.getAllFAQs();
        List<MainNewsEntity> mainNewsEntities = mainNewsRepository.findAll();

        // Response 조합 (기존 구조 유지)
        return GetAdminResponseDto.builder()
                .generation(generation.id())
                .name(generation.name())
                // ... (기존 GetAdminResponseDto 구조에 맞춰 변환)
                .build();
    }

    // MainNews 관련 메서드들은 기존 유지
    @Override
    public AddAdminNewsResponseDto addMainNews(AddAdminNewsRequestDto request) {
        log.info("Adding main news: {}", request.getTitle());

        // 1. S3에 이미지 업로드
        String imageUrl = s3Service.uploadFile(request.getImage(), "news/");

        // 2. MainNews 엔티티 생성 및 저장
        MainNewsEntity newsEntity = new MainNewsEntity();
        newsEntity.setTitle(request.getTitle());
        newsEntity.setLink(request.getLink());
        newsEntity.setImage(imageUrl);

        mainNewsRepository.save(newsEntity);

        log.info("Main news added successfully: {}", newsEntity.getId());

        return AddAdminNewsResponseDto.builder()
                .message("최신소식 추가 성공")
                .build();
    }

    @Override
    public DeleteAdminNewsResponseDto deleteMainNews(DeleteAdminNewsRequestDto request) {
        log.info("Deleting main news: {}", request.getId());

        // 1. MainNews 조회
        MainNewsEntity newsEntity = mainNewsRepository.findById(request.getId());
        if (newsEntity == null) {
            throw new sopt.org.homepage.exception.ClientBadRequestException(
                    "News not found with id: " + request.getId());
        }

        // 2. S3에서 이미지 삭제
        s3Service.deleteFile(newsEntity.getImage());

        // 3. DB에서 삭제
        mainNewsRepository.delete(newsEntity);

        log.info("Main news deleted successfully: {}", request.getId());

        return DeleteAdminNewsResponseDto.builder()
                .message("최신소식 삭제 성공")
                .build();
    }

    @Override
    public GetAdminNewsResponseDto getMainNews(GetAdminNewsRequestDto request) {
        log.info("Getting main news: {}", request.getId());

        // 1. MainNews 조회
        MainNewsEntity newsEntity = mainNewsRepository.findById(request.getId());
        if (newsEntity == null) {
            throw new sopt.org.homepage.exception.ClientBadRequestException(
                    "News not found with id: " + request.getId());
        }

        // 2. Response 생성
        return GetAdminNewsResponseDto.builder()
                .id(newsEntity.getId())
                .title(newsEntity.getTitle())
                .link(newsEntity.getLink())
                .image(newsEntity.getImage())
                .build();
    }

    /**
     * 캐시용 데이터 클래스
     */
    @lombok.Data
    public static class CachedAdminData {
        private Integer generationId;
        private String name;
        private String headerImageUrl;
        private String recruitHeaderImageUrl;
        private BrandingColorData brandingColor;
        private MainButtonData mainButton;
        private List<CoreValueData> coreValues;
        private List<MemberData> members;
        private List<PartIntroductionData> partIntroductions;
        private List<PartCurriculumData> partCurriculums;
        private List<RecruitScheduleData> recruitSchedules;
        private List<RecruitPartCurriculumData> recruitPartCurriculums;
        private List<RecruitQuestionData> recruitQuestions;

        @lombok.Builder
        @lombok.Data
        public static class BrandingColorData {
            private String main;
            private String low;
            private String high;
            private String point;
        }

        @lombok.Builder
        @lombok.Data
        public static class MainButtonData {
            private String text;
            private String keyColor;
            private String subColor;
        }

        @lombok.Builder
        @lombok.Data
        public static class CoreValueData {
            private String value;
            private String description;
            private String imageUrl;
        }

        @lombok.Builder
        @lombok.Data
        public static class MemberData {
            private String role;
            private String name;
            private String affiliation;
            private String introduction;
            private String profileImageUrl;
            private SnsLinksData sns;
        }

        @lombok.Builder
        @lombok.Data
        public static class SnsLinksData {
            private String email;
            private String linkedin;
            private String github;
            private String behance;
        }

        @lombok.Builder
        @lombok.Data
        public static class PartIntroductionData {
            private String part;
            private String description;
        }

        @lombok.Builder
        @lombok.Data
        public static class PartCurriculumData {
            private String part;
            private List<String> curriculums;
        }

        @lombok.Builder
        @lombok.Data
        public static class RecruitScheduleData {
            private String type;
            private ScheduleData schedule;
        }

        @lombok.Builder
        @lombok.Data
        public static class ScheduleData {
            private String applicationStartTime;
            private String applicationEndTime;
            private String applicationResultTime;
            private String interviewStartTime;
            private String interviewEndTime;
            private String finalResultTime;
        }

        @lombok.Builder
        @lombok.Data
        public static class RecruitPartCurriculumData {
            private String part;
            private IntroductionData introduction;
        }

        @lombok.Builder
        @lombok.Data
        public static class IntroductionData {
            private String content;
            private String preference;
        }

        @lombok.Builder
        @lombok.Data
        public static class RecruitQuestionData {
            private String part;
            private List<QuestionData> question;
        }

        @lombok.Builder
        @lombok.Data
        public static class QuestionData {
            private String question;
            private String answer;
        }
    }
}