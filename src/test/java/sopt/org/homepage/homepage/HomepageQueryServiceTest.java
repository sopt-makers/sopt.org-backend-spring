package sopt.org.homepage.homepage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sopt.org.homepage.activityschedule.ActivityScheduleService;
import sopt.org.homepage.application.homepage.review.service.HomepageReviewService;
import sopt.org.homepage.application.homepage.service.HomepageQueryService;
import sopt.org.homepage.corevalue.CoreValueService;
import sopt.org.homepage.faq.FAQService;
import sopt.org.homepage.generation.GenerationService;
import sopt.org.homepage.generation.dto.GenerationDetailView;
import sopt.org.homepage.infrastructure.external.auth.AuthService;
import sopt.org.homepage.infrastructure.external.crew.CrewService;
import sopt.org.homepage.infrastructure.external.playground.PlaygroundService;
import sopt.org.homepage.member.MemberService;
import sopt.org.homepage.news.NewsService;
import sopt.org.homepage.part.PartService;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;
import sopt.org.homepage.recruitment.RecruitmentService;
import sopt.org.homepage.recruitpartintroduction.RecruitPartIntroductionService;

@ExtendWith(MockitoExtension.class)
class HomepageQueryServiceTest {

    @Mock ActivityScheduleService activityScheduleService;
    @Mock CoreValueService coreValueService;
    @Mock FAQService faqService;
    @Mock GenerationService generationService;
    @Mock MemberService memberService;
    @Mock PartService partService;
    @Mock RecruitmentService recruitmentService;
    @Mock RecruitPartIntroductionService recruitPartIntroductionService;
    @Mock HomepageReviewService homepageReviewService;
    @Mock NewsService newsService;
    @Mock CrewService crewService;
    @Mock PlaygroundService playgroundService;
    @Mock AuthService authService;
    @InjectMocks HomepageQueryService service;

    @Test
    @DisplayName("✅ 회원과 프로젝트 수는 직전 기수, 창업과 이용 가능 수는 전체 기수에서 집계한다")
    void aggregatesRecordsAcrossTheIntendedGenerations() {
        when(generationService.findLatest()).thenReturn(GenerationDetailView.builder()
                .id(39)
                .brandingColor(GenerationDetailView.BrandingColorView.builder().build())
                .build());
        when(authService.getUserCountByGeneration(38)).thenReturn(150);
        when(crewService.getStudyCount(38)).thenReturn(10);
        when(playgroundService.getAllProjects(any())).thenReturn(List.of(
                ProjectsResponseDto.builder().generation(38).isFounding(true).isAvailable(true).build(),
                ProjectsResponseDto.builder().generation(37).isFounding(true).isAvailable(false).build(),
                ProjectsResponseDto.builder().generation(39).isFounding(false).isAvailable(true).build(),
                ProjectsResponseDto.builder().generation(null).isFounding(null).isAvailable(null).build()));

        var records = service.getMainPageData().activitiesRecords();

        assertThat(records.activitiesMemberCount()).isEqualTo(150);
        assertThat(records.projectCounts()).isEqualTo(1);
        assertThat(records.studyCounts()).isEqualTo(10);
        assertThat(records.foundingTeamCount()).isEqualTo(2);
        assertThat(records.availableServiceCount()).isEqualTo(2);
        verify(authService).getUserCountByGeneration(38);
    }
}
