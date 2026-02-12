package sopt.org.homepage.project;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sopt.org.homepage.global.common.dto.PaginateResponseDto;
import sopt.org.homepage.global.common.util.ArrayUtil;
import sopt.org.homepage.infrastructure.external.playground.PlaygroundService;
import sopt.org.homepage.project.dto.request.GetProjectsRequestDto;
import sopt.org.homepage.project.dto.response.ProjectDetailResponseDto;
import sopt.org.homepage.project.dto.response.ProjectsResponseDto;

@ExtendWith(MockitoExtension.class) // Mockito(Mock 라이브러리) 활성화
@DisplayName("Project 서비스 테스트")
class ProjectServiceTest {

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
