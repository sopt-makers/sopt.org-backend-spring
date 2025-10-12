package sopt.org.homepage.homepage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.corevalue.service.query.CoreValueQueryService;
import sopt.org.homepage.corevalue.service.query.dto.CoreValueView;
import sopt.org.homepage.faq.service.query.FAQQueryService;
import sopt.org.homepage.faq.service.query.dto.FAQView;
import sopt.org.homepage.generation.service.query.GenerationQueryService;
import sopt.org.homepage.generation.service.query.dto.GenerationDetailView;
import sopt.org.homepage.homepage.controller.dto.AboutPageResponse;
import sopt.org.homepage.homepage.controller.dto.MainPageResponse;
import sopt.org.homepage.homepage.controller.dto.RecruitPageResponse;
import sopt.org.homepage.internal.crew.CrewService;
import sopt.org.homepage.internal.playground.PlaygroundService;
import sopt.org.homepage.main.entity.MainNewsEntity;
import sopt.org.homepage.main.repository.MainNewsRepository;
import sopt.org.homepage.member.service.query.MemberQueryService;
import sopt.org.homepage.member.service.query.dto.MemberDetailView;
import sopt.org.homepage.part.service.query.PartQueryService;
import sopt.org.homepage.part.service.query.dto.PartCurriculumView;
import sopt.org.homepage.part.service.query.dto.PartIntroductionView;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;
import sopt.org.homepage.recruitment.service.query.RecruitPartIntroductionQueryService;
import sopt.org.homepage.recruitment.service.query.RecruitmentQueryService;
import sopt.org.homepage.recruitment.service.query.dto.RecruitPartIntroductionView;
import sopt.org.homepage.recruitment.service.query.dto.RecruitmentView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * HomepageQueryService
 *
 * 책임:
 * - 여러 도메인을 조합하여 페이지별 응답 생성
 * - 기존 API Response 구조 유지
 * - User API용 (Admin과 분리)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HomepageQueryService {

    // Domain Query Services
    private final GenerationQueryService generationQueryService;
    private final CoreValueQueryService coreValueQueryService;
    private final MemberQueryService memberQueryService;
    private final PartQueryService partQueryService;
    private final RecruitmentQueryService recruitmentQueryService;
    private final RecruitPartIntroductionQueryService recruitPartIntroductionQueryService;
    private final FAQQueryService faqQueryService;

    // Legacy Repositories & Services
    private final MainNewsRepository mainNewsRepository;
    private final CrewService crewService;
    private final PlaygroundService playgroundService;

    /**
     * Main 페이지 데이터 조회
     *
     * GET /homepage
     */
    public MainPageResponse getMainPageData() {
        log.info("Querying main page data");

        // 1. Generation 정보 조회
        GenerationDetailView generation = generationQueryService.getLatestGeneration();
        Integer generationId = generation.id();

        // 2. Part Introduction 조회
        List<PartIntroductionView> partIntroductions =
                partQueryService.getPartIntroductionsByGeneration(generationId);

        // 3. Latest News 조회
        List<MainNewsEntity> mainNewsEntities = mainNewsRepository.findAll();

        // 4. Recruitment Schedule 조회
        List<RecruitmentView> recruitments =
                recruitmentQueryService.getRecruitmentsByGeneration(generationId);

        // 5. Response 조합
        return MainPageResponse.builder()
                .generation(generation.id())
                .name(generation.name())
                .brandingColor(MainPageResponse.BrandingColor.builder()
                        .main(generation.brandingColor().main())
                        .high(generation.brandingColor().high())
                        .low(generation.brandingColor().low())
                        .point(generation.brandingColor().background())
                        .build())
                .mainButton(MainPageResponse.MainButton.builder()
                        .text(generation.mainButton().text())
                        .keyColor(generation.mainButton().keyColor())
                        .subColor(generation.mainButton().subColor())
                        .build())
                .partIntroduction(partIntroductions.stream()
                        .map(pi -> MainPageResponse.PartIntroduction.builder()
                                .part(pi.part())
                                .description(pi.description())
                                .build())
                        .toList())
                .latestNews(mainNewsEntities.stream()
                        .map(news -> MainPageResponse.LatestNews.builder()
                                .id(news.getId())
                                .title(news.getTitle())
                                .image(news.getImage())
                                .link(news.getLink())
                                .build())
                        .toList())
                .recruitSchedule(recruitments.stream()
                        .map(r -> MainPageResponse.RecruitSchedule.builder()
                                .type(r.type())
                                .schedule(MainPageResponse.RecruitSchedule.Schedule.builder()
                                        .applicationStartTime(r.schedule().applicationStartTime())
                                        .applicationEndTime(r.schedule().applicationEndTime())
                                        .applicationResultTime(r.schedule().applicationResultTime())
                                        .interviewStartTime(r.schedule().interviewStartTime())
                                        .interviewEndTime(r.schedule().interviewEndTime())
                                        .finalResultTime(r.schedule().finalResultTime())
                                        .build())
                                .build())
                        .toList())
                .build();
    }

    /**
     * About 페이지 데이터 조회
     *
     * GET /homepage/about
     */
    public AboutPageResponse getAboutPageData() {
        log.info("Querying about page data");

        // 1. Generation 정보 조회
        GenerationDetailView generation = generationQueryService.getLatestGeneration();
        Integer generationId = generation.id();

        // 2. Core Values 조회
        List<CoreValueView> coreValues =
                coreValueQueryService.getCoreValuesByGeneration(generationId);

        // 3. Part Curriculums 조회
        List<PartCurriculumView> partCurriculums =
                partQueryService.getPartCurriculumsByGeneration(generationId);

        // 4. Members 조회
        List<MemberDetailView> members =
                memberQueryService.getMembersByGeneration(generationId);

        // 5. Activities Records 조회 (Playground API)
        AboutPageResponse.ActivitiesRecords activitiesRecords =
                getActivitiesRecords(generationId);

        // 6. Response 조합
        return AboutPageResponse.builder()
                .generation(generation.id())
                .name(generation.name())
                .headerImage(generation.headerImage())
                .brandingColor(AboutPageResponse.BrandingColor.builder()
                        .main(generation.brandingColor().main())
                        .high(generation.brandingColor().high())
                        .low(generation.brandingColor().low())
                        .point(generation.brandingColor().background())
                        .build())
                .coreValue(coreValues.stream()
                        .map(cv -> AboutPageResponse.CoreValue.builder()
                                .value(cv.value())
                                .description(cv.description())
                                .image(cv.imageUrl())
                                .build())
                        .toList())
                .partCurriculum(partCurriculums.stream()
                        .map(pc -> AboutPageResponse.PartCurriculum.builder()
                                .part(pc.part())
                                .curriculums(pc.curriculums())
                                .build())
                        .toList())
                .member(members.stream()
                        .map(m -> AboutPageResponse.Member.builder()
                                .role(m.role())
                                .name(m.name())
                                .affiliation(m.affiliation())
                                .introduction(m.introduction())
                                .profileImage(m.profileImageUrl())
                                .sns(AboutPageResponse.Member.SnsLinks.builder()
                                        .email(m.snsLinks().email())
                                        .linkedin(m.snsLinks().linkedin())
                                        .github(m.snsLinks().github())
                                        .behance(m.snsLinks().behance())
                                        .build())
                                .build())
                        .toList())
                .activitiesRecords(activitiesRecords)
                .build();
    }

    /**
     * Recruit 페이지 데이터 조회
     *
     * GET /homepage/recruit
     */
    public RecruitPageResponse getRecruitPageData() {
        log.info("Querying recruit page data");

        // 1. Generation 정보 조회
        GenerationDetailView generation = generationQueryService.getLatestGeneration();
        Integer generationId = generation.id();

        // 2. Recruitment Schedules 조회
        List<RecruitmentView> recruitments =
                recruitmentQueryService.getRecruitmentsByGeneration(generationId);

        // 3. Recruit Part Introductions 조회
        List<RecruitPartIntroductionView> recruitPartIntros =
                recruitPartIntroductionQueryService.getRecruitPartIntroductionsByGeneration(generationId);

        // 4. FAQs 조회
        List<FAQView> faqs = faqQueryService.getAllFAQs();

        // 5. Response 조합
        return RecruitPageResponse.builder()
                .generation(generation.id())
                .name(generation.name())
                .recruitHeaderImage(generation.recruitHeaderImage())
                .brandingColor(RecruitPageResponse.BrandingColor.builder()
                        .main(generation.brandingColor().main())
                        .high(generation.brandingColor().high())
                        .low(generation.brandingColor().low())
                        .point(generation.brandingColor().background())
                        .build())
                .recruitSchedule(recruitments.stream()
                        .map(r -> RecruitPageResponse.RecruitSchedule.builder()
                                .type(r.type())
                                .schedule(RecruitPageResponse.RecruitSchedule.Schedule.builder()
                                        .applicationStartTime(r.schedule().applicationStartTime())
                                        .applicationEndTime(r.schedule().applicationEndTime())
                                        .applicationResultTime(r.schedule().applicationResultTime())
                                        .interviewStartTime(r.schedule().interviewStartTime())
                                        .interviewEndTime(r.schedule().interviewEndTime())
                                        .finalResultTime(r.schedule().finalResultTime())
                                        .build())
                                .build())
                        .toList())
                .recruitPartCurriculum(recruitPartIntros.stream()
                        .map(rpi -> RecruitPageResponse.RecruitPartCurriculum.builder()
                                .part(rpi.part())
                                .introduction(RecruitPageResponse.RecruitPartCurriculum.Introduction.builder()
                                        .content(rpi.introduction().content())
                                        .preference(rpi.introduction().preference())
                                        .build())
                                .build())
                        .toList())
                .recruitQuestion(faqs.stream()
                        .map(faq -> RecruitPageResponse.RecruitQuestion.builder()
                                .part(faq.part())
                                .question(faq.questions().stream()
                                        .map(q -> RecruitPageResponse.RecruitQuestion.Question.builder()
                                                .question(q.question())
                                                .answer(q.answer())
                                                .build())
                                        .toList())
                                .build())
                        .toList())
                .build();
    }

    /**
     * Activities Records 조회 (Playground API 호출)
     */
    private AboutPageResponse.ActivitiesRecords getActivitiesRecords(Integer generationId) {
        try {


            var allProjects = playgroundService.getAllProjects(
                    new GetProjectsRequestDto(1, Integer.MAX_VALUE, null, null)
            );

            int projectCount=allProjects.stream()
                    .filter(project -> project.getGeneration() != null &&
                            project.getGeneration().equals(generationId))
                    .toList().size();


            // Study 개수 조회 (Crew API)
            int studyCount = crewService.getStudyCount(generationId);

            return AboutPageResponse.ActivitiesRecords.builder()
                    .projectCount(projectCount)
                    .studyCount(studyCount)
                    .build();
        } catch (Exception e) {
            log.warn("Failed to get activities records for generation {}: {}",
                    generationId, e.getMessage());

            // 실패 시 기본값 반환
            return AboutPageResponse.ActivitiesRecords.builder()
                    .projectCount(0)
                    .studyCount(0)
                    .build();
        }
    }
}