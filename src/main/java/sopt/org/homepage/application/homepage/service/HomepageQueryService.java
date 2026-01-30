package sopt.org.homepage.application.homepage.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.application.homepage.controller.dto.AboutPageResponse;
import sopt.org.homepage.application.homepage.controller.dto.MainPageResponse;
import sopt.org.homepage.application.homepage.controller.dto.RecruitPageResponse;
import sopt.org.homepage.corevalue.CoreValueService;
import sopt.org.homepage.corevalue.dto.CoreValueView;
import sopt.org.homepage.faq.FAQService;
import sopt.org.homepage.faq.dto.FAQView;
import sopt.org.homepage.generation.GenerationService;
import sopt.org.homepage.generation.dto.GenerationDetailView;
import sopt.org.homepage.infrastructure.external.auth.AuthService;
import sopt.org.homepage.infrastructure.external.crew.CrewService;
import sopt.org.homepage.infrastructure.external.playground.PlaygroundService;
import sopt.org.homepage.member.MemberService;
import sopt.org.homepage.member.dto.MemberDetailView;
import sopt.org.homepage.news.News;
import sopt.org.homepage.news.NewsService;
import sopt.org.homepage.part.PartService;
import sopt.org.homepage.part.dto.PartCurriculumView;
import sopt.org.homepage.part.dto.PartIntroductionView;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.recruitment.RecruitmentService;
import sopt.org.homepage.recruitment.dto.RecruitmentView;
import sopt.org.homepage.recruitpartintroduction.RecruitPartIntroductionService;
import sopt.org.homepage.recruitpartintroduction.dto.RecruitPartIntroductionView;

/**
 * HomepageQueryService
 * <p>
 * 책임: - 여러 도메인을 조합하여 페이지별 응답 생성 - 기존 API Response 구조 유지 - User API용 (Admin과 분리)
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class HomepageQueryService {
    private final CoreValueService coreValueService;
    private final FAQService faqService;
    private final GenerationService generationService;
    private final MemberService memberService;
    private final PartService partService;
    private final RecruitmentService recruitmentService;
    private final RecruitPartIntroductionService recruitPartIntroductionService;


    // Legacy Repositories & Services
    private final NewsService newsService;
    private final CrewService crewService;
    private final PlaygroundService playgroundService;
    private final AuthService authService;

    /**
     * Main 페이지 데이터 조회
     * <p>
     * GET /homepage
     */
    public MainPageResponse getMainPageData() {
        log.info("Querying main page data");

        // 1. Generation 정보 조회
        GenerationDetailView generation = generationService.findLatest();
        Integer generationId = generation.id();

        // 2. Part Introduction 조회
        List<PartIntroductionView> partIntroductions =
                partService.findIntroductionsByGeneration(generationId);

        // 3. Latest News 조회
        List<News> newsEntities = newsService.findAll();

        // 4. Recruitment Schedule 조회
        List<RecruitmentView> recruitments =
                recruitmentService.findByGeneration(generationId);

        // 5. Activities Records 조회 (Playground API)
        MainPageResponse.ActivitiesRecords activitiesRecords =
                getActivitiesRecords(generationId - 1);

        // 6. Response 조합
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
                .latestNews(newsEntities.stream()
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
                .activitiesRecords(activitiesRecords)
                .build();
    }

    /**
     * About 페이지 데이터 조회
     * <p>
     * GET /homepage/about
     */
    public AboutPageResponse getAboutPageData() {
        log.info("Querying about page data");

        // 1. Generation 정보 조회
        GenerationDetailView generation = generationService.findLatest();
        Integer generationId = generation.id();

        // 2. Core Values 조회
        List<CoreValueView> coreValues =
                coreValueService.findByGeneration(generationId);

        // 3. Part Curriculums 조회
        List<PartCurriculumView> partCurriculums =
                partService.findCurriculumsByGeneration(generationId);

        // 4. Members 조회
        List<MemberDetailView> members =
                memberService.findByGeneration(generationId);

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
                .build();
    }

    /**
     * Recruit 페이지 데이터 조회
     * <p>
     * GET /homepage/recruit
     */
    public RecruitPageResponse getRecruitPageData() {
        log.info("Querying recruit page data");

        // 1. Generation 정보 조회
        GenerationDetailView generation = generationService.findLatest();
        Integer generationId = generation.id();

        // 2. Recruitment Schedules 조회
        List<RecruitmentView> recruitments =
                recruitmentService.findByGeneration(generationId);

        // 3. Recruit Part Introductions 조회
        List<RecruitPartIntroductionView> recruitPartIntros =
                recruitPartIntroductionService.findByGeneration(generationId);

        // 4. FAQs 조회
        List<FAQView> faqs = faqService.findAll();

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
                                .part(faq.part().getValue())
                                .questions(faq.questions().stream()
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
    private MainPageResponse.ActivitiesRecords getActivitiesRecords(Integer generationId) {
        try {

            // 1. 활동 회원 수 조회 (Generation별 Member 수)
            long activitiesMemberCount = authService.getUserCountByGeneration(generationId);

            var allProjects = playgroundService.getAllProjects(
                    new GetProjectsRequestDto(1, Integer.MAX_VALUE, null, null)
            );

            int projectCount = allProjects.stream()
                    .filter(project -> project.getGeneration() != null &&
                            project.getGeneration().equals(generationId))
                    .toList().size();

            // Study 개수 조회 (Crew API)
            int studyCount = crewService.getStudyCount(generationId);

            return MainPageResponse.ActivitiesRecords.builder()
                    .activitiesMemberCount((int) activitiesMemberCount)
                    .projectCounts(projectCount)
                    .studyCounts(studyCount)
                    .build();

        } catch (Exception e) {
            log.warn("Failed to get activities records for generation {}: {}",
                    generationId, e.getMessage());

            // 실패 시 기본값 반환
            return MainPageResponse.ActivitiesRecords.builder()
                    .activitiesMemberCount(0)
                    .projectCounts(0)
                    .studyCounts(0)
                    .build();
        }
    }
}
