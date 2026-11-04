package sopt.org.homepage.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import sopt.org.homepage.global.common.dto.PaginateResponseDto;
import sopt.org.homepage.global.common.util.ArrayUtil;
import sopt.org.homepage.infrastructure.external.playground.PlaygroundService;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;
import sopt.org.homepage.project.dto.type.ProjectType;

@ExtendWith(MockitoExtension.class) // Mockito(Mock 라이브러리) 활성화
@DisplayName("Project 서비스 테스트")
class ProjectServiceTest {

    @Test
    @DisplayName("✅ 프로젝트 조회 파라미터가 요청 객체에 바인딩된다")
    void bindsQueryParameters() throws Exception {
        ProjectService service = mock(ProjectService.class);
        when(service.paginateProjects(any())).thenReturn(new PaginateResponseDto<>(List.of(), 0, 5, 1));
        var mvc = MockMvcBuilders.standaloneSetup(new ProjectController(service)).build();

        mvc.perform(get("/projects").param("filter", "APPJAM").param("generation", "39")
                .param("isAvailable", "true").param("isFounding", "false")
                .param("sortBy", "NAME").param("sortDirection", "ASC").param("limit", "5"))
                .andExpect(status().isOk());

        var request = ArgumentCaptor.forClass(GetProjectsRequestDto.class);
        verify(service).paginateProjects(request.capture());
        assertThat(request.getValue().getGeneration()).isEqualTo(39);
        assertThat(request.getValue().getIsAvailable()).isTrue();
        assertThat(request.getValue().getIsFounding()).isFalse();
        assertThat(request.getValue().getSortBy()).isEqualTo(GetProjectsRequestDto.SortBy.NAME);
        assertThat(request.getValue().getSortDirection()).isEqualTo(GetProjectsRequestDto.SortDirection.ASC);
        assertThat(request.getValue().getLimit()).isEqualTo(5);
        assertThat(request.getValue().getFilter()).isEqualTo(ProjectType.APPJAM);
    }

    @Test
    @DisplayName("❌ 잘못된 조회 파라미터는 내부 구현 정보 없이 반환한다")
    void rejectsInvalidParametersWithoutInternalDetails() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new ProjectController(mock(ProjectService.class))).build();
        for (String field : List.of("sortBy", "sortDirection", "generation")) {
            mvc.perform(get("/projects").param(field, "invalid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("Invalid parameter: " + field));
        }
        mvc.perform(get("/projects").param("sortBy", "name"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid parameter: sortBy"));
        mvc.perform(get("/projects").param("sortDirection", "asc"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid parameter: sortDirection"));
    }

    @Mock
    private PlaygroundService playgroundService; // 가짜 PlaygroundService

    @Mock
    private ArrayUtil arrayUtil; // 가짜 ArrayUtil

    @InjectMocks
    private ProjectService projectService; // 진짜 ProjectService 위의 Mock들을 자동으로 주입해줌

    @Nested
    @DisplayName("프로젝트 목록 조회")
    class PaginateProjects {

        @Test
        @DisplayName("✅ 필터와 정렬을 적용한 뒤 요청한 개수만 반환")
        void filtersAndSortsBeforePagination() {
            var service = new ProjectService(playgroundService, new ArrayUtil());
            when(playgroundService.getAllProjects(any())).thenReturn(List.of(
                    ProjectsResponseDto.builder().id(3L).name("다").generation(39).isAvailable(true).isFounding(true).build(),
                    ProjectsResponseDto.builder().id(1L).name("가").generation(39).isAvailable(true).isFounding(true).build(),
                    ProjectsResponseDto.builder().id(2L).name("나").generation(39).isAvailable(false).isFounding(true).build(),
                    ProjectsResponseDto.builder().id(4L).name("라").generation(38).isAvailable(true).isFounding(true).build(),
                    ProjectsResponseDto.builder().id(5L).name("마").generation(39).isAvailable(true).isFounding(false).build()
            ));
            var request = new GetProjectsRequestDto(1, 1, null, null);
            request.setGeneration(39);
            request.setIsAvailable(true);
            request.setIsFounding(true);
            request.setSortBy(GetProjectsRequestDto.SortBy.NAME);
            request.setSortDirection(GetProjectsRequestDto.SortDirection.ASC);

            var result = service.paginateProjects(request);

            assertThat(result.getData()).extracting(ProjectsResponseDto::getName).containsExactly("가");
            assertThat(result.getTotalCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ 운영 중이 아닌 프로젝트를 이름 내림차순으로 조회한다")
        void filtersUnavailableAndSortsNameDescending() {
            var service = new ProjectService(playgroundService, new ArrayUtil());
            when(playgroundService.getAllProjects(any())).thenReturn(List.of(
                    ProjectsResponseDto.builder().id(1L).name("가").isAvailable(false).build(),
                    ProjectsResponseDto.builder().id(2L).name("나").isAvailable(false).build(),
                    ProjectsResponseDto.builder().id(3L).name("다").isAvailable(true).build()));
            var request = new GetProjectsRequestDto(1, 10, null, null);
            request.setIsAvailable(false);
            request.setSortBy(GetProjectsRequestDto.SortBy.NAME);
            request.setSortDirection(GetProjectsRequestDto.SortDirection.DESC);

            var result = service.paginateProjects(request);
            assertThat(result.getData()).extracting(ProjectsResponseDto::getName).containsExactly("나", "가");
            assertThat(result.getTotalCount()).isEqualTo(2);
        }

        @Test
        @DisplayName("✅ 같은 기수는 원본 순서를 유지하며 null 기수는 오름차순에서도 마지막")
        void stableAscendingOrderWithNullGeneration() {
            var service = new ProjectService(playgroundService, new ArrayUtil());
            when(playgroundService.getAllProjects(any())).thenReturn(List.of(
                    createProject(3L, "C", null), createProject(2L, "B", 39),
                    createProject(1L, "A", 39), createProject(4L, "D", 38)));
            var request = new GetProjectsRequestDto(1, 10, null, null);
            request.setSortDirection(GetProjectsRequestDto.SortDirection.ASC);

            assertThat(service.paginateProjects(request).getData())
                    .extracting(ProjectsResponseDto::getId).containsExactly(4L, 2L, 1L, 3L);
        }

        @Test
        @DisplayName("✅ 기본 정렬에서 같은 기수는 Playground 순서를 유지한다")
        void defaultSortPreservesOrderForSameGeneration() {
            var service = new ProjectService(playgroundService, new ArrayUtil());
            when(playgroundService.getAllProjects(any())).thenReturn(List.of(
                    createProject(3L, "C", 39), createProject(2L, "B", 39),
                    createProject(1L, "A", 39)));

            var request = new GetProjectsRequestDto(1, 10, null, null);
            assertThat(service.paginateProjects(request).getData())
                    .extracting(ProjectsResponseDto::getId).containsExactly(3L, 2L, 1L);
        }

        @Test
        @DisplayName("✅ 정상: generation 내림차순 정렬 후 페이지네이션")
        void paginateProjects_SortedByGenerationDesc() {
            // given     정렬 안 된 프로젝트 3개를 만듦
            List<ProjectsResponseDto> unsorted = List.of(
                    createProject(1L, "프로젝트A", 33),
                    createProject(2L, "프로젝트B", 35),
                    createProject(3L, "프로젝트C", 34)
            );
            // "playgroundService.getAllProjects()가 호출되면 unsorted를 반환해라"
            //  new ArrayList로 감싸는 이유:List.of()는 불변이라 sort()가 안 됨
            when(playgroundService.getAllProjects(any()))
                    .thenReturn(new ArrayList<>(unsorted));

            // "arrayUtil.paginateArray()가 호출되면 받은 리스트를 그대로 반환해라"
            //  thenAnswer: 첫 번째 인자(정렬된 리스트)를 그대로 반환 → 페이지네이션 로직은 여기서 검증 대상이 아니니까 통과
            when(arrayUtil.paginateArray(anyList(), eq(1), eq(10)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            GetProjectsRequestDto request = new GetProjectsRequestDto(1, 10, null, null);

            // when     // 실제 테스트 대상 메서드 호출
            PaginateResponseDto<ProjectsResponseDto> result =
                    projectService.paginateProjects(request);

            // then - 35기 → 34기 → 33기 순서
            // 정렬 결과 확인: 35 → 34 → 33 순서여야 함
            assertThat(result.getData())
                    .extracting(ProjectsResponseDto::getGeneration)
                    .containsExactly(35, 34, 33);
            // extracting: 각 객체에서 generation 값만 추출 , containsExactly: 정확히 이 순서로 있어야 함
            assertThat(result.getTotalCount()).isEqualTo(3);
        }

        @Test
        @DisplayName("✅ 정상: generation이 null인 프로젝트는 맨 뒤로")
        void paginateProjects_NullGenerationLast() {
            // given
            List<ProjectsResponseDto> projects = new ArrayList<>(List.of(
                    createProject(1L, "프로젝트A", null),
                    createProject(2L, "프로젝트B", 35),
                    createProject(3L, "프로젝트C", 33)
            ));
            when(playgroundService.getAllProjects(any())).thenReturn(projects);
            when(arrayUtil.paginateArray(anyList(), eq(1), eq(10)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            GetProjectsRequestDto request = new GetProjectsRequestDto(1, 10, null, null);

            // when
            PaginateResponseDto<ProjectsResponseDto> result =
                    projectService.paginateProjects(request);

            // then
            assertThat(result.getData())
                    .extracting(ProjectsResponseDto::getGeneration)
                    .containsExactly(35, 33, null);
        }

        @Test
        @DisplayName("✅ 정상: Playground API 결과가 비어있으면 빈 목록")
        void paginateProjects_EmptyResult() {
            // given
            when(playgroundService.getAllProjects(any()))
                    .thenReturn(new ArrayList<>());
            when(arrayUtil.paginateArray(anyList(), eq(1), eq(10)))
                    .thenReturn(List.of());

            GetProjectsRequestDto request = new GetProjectsRequestDto(1, 10, null, null);

            // when
            PaginateResponseDto<ProjectsResponseDto> result =
                    projectService.paginateProjects(request);

            // then
            assertThat(result.getData()).isEmpty();
            assertThat(result.getTotalCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("프로젝트 상세 조회")
    class FindOne {

        @Test
        @DisplayName("✅ 정상: 프로젝트 상세 조회")
        void findOne_Success() {
            // given
            ProjectDetailResponseDto expected = ProjectDetailResponseDto.builder()
                    .id(1L)
                    .name("테스트 프로젝트")
                    .generation(35)
                    .build();
            when(playgroundService.getProjectDetail(1L)).thenReturn(expected);

            // when
            ProjectDetailResponseDto result = projectService.findOne(1L);

            // then
            assertThat(result.getName()).isEqualTo("테스트 프로젝트");
            assertThat(result.getGeneration()).isEqualTo(35);
        }
    }

    // ===== Helper Methods =====

    private ProjectsResponseDto createProject(Long id, String name, Integer generation) {
        return ProjectsResponseDto.builder()
                .id(id)
                .name(name)
                .generation(generation)
                .build();
    }
}
