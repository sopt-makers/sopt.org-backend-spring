package sopt.org.homepage.part;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.part.dto.BulkCreatePartsCommand;
import sopt.org.homepage.part.dto.PartCurriculumView;
import sopt.org.homepage.part.dto.PartDetailView;
import sopt.org.homepage.part.dto.PartIntroductionView;

/**
 * Part 통합 테스트
 * <p>
 * 인수인계 목적: - Part는 SOPT 파트를 나타냄 (ANDROID, IOS, WEB, SERVER, PLAN, DESIGN) - 기수(generationId)별로 관리됨 - curriculums는
 * JSON으로 저장 (List<String>) - BulkCreate에서 PartIntroduction + PartCurriculum 병합 - PartType.getValue()는 한글 반환 (안드로이드,
 * iOS, 웹, 서버, 기획, 디자인)
 */
@DisplayName("Part 서비스 통합 테스트")
class PartServiceTest extends IntegrationTestBase {

    @Autowired
    private PartService partService;

    @Autowired
    private PartRepository partRepository;

    @AfterEach
    void tearDown() {
        partRepository.deleteAll();
    }

    // ===== 생성 시나리오 =====

    @Nested
    @DisplayName("파트 생성")
    class Create {

        @Test
        @DisplayName("✅ 정상: 일괄 생성 (Introduction + Curriculum 병합)")
        void bulkCreate_MergesData() {
            // given
            BulkCreatePartsCommand command = BulkCreatePartsCommand.builder()
                    .generationId(35)
                    .partIntroductions(List.of(
                            BulkCreatePartsCommand.PartData.builder()
                                    .part("안드로이드")  // ✅ 한글
                                    .description("안드로이드 소개")
                                    .build(),
                            BulkCreatePartsCommand.PartData.builder()
                                    .part("iOS")
                                    .description("iOS 소개")
                                    .build()
                    ))
                    .partCurriculums(List.of(
                            BulkCreatePartsCommand.PartCurriculumData.builder()
                                    .part("안드로이드")  // ✅ 한글
                                    .curriculums(List.of("1주차", "2주차"))
                                    .build(),
                            BulkCreatePartsCommand.PartCurriculumData.builder()
                                    .part("iOS")
                                    .curriculums(List.of("1주차 Swift", "2주차 UIKit"))
                                    .build()
                    ))
                    .build();

            // when
            List<Long> ids = partService.bulkCreate(command);

            // then
            assertThat(ids).hasSize(2);

            List<PartDetailView> result = partService.findByGeneration(35);
            assertThat(result).hasSize(2);

            // Android 파트 검증
            PartDetailView android = result.stream()
                    .filter(p -> p.part().equals("안드로이드"))  // ✅ 한글
                    .findFirst()
                    .orElseThrow();
            assertThat(android.description()).isEqualTo("안드로이드 소개");
            assertThat(android.curriculums()).containsExactly("1주차", "2주차");
        }

        @Test
        @DisplayName("✅ 정상: 일괄 생성 시 기존 데이터 삭제")
        void bulkCreate_ReplacesExisting() {
            // given - 기존 데이터
            partRepository.save(createEntity(35, PartType.WEB));
            assertThat(partRepository.countByGenerationId(35)).isEqualTo(1);

            // when
            BulkCreatePartsCommand command = BulkCreatePartsCommand.builder()
                    .generationId(35)
                    .partIntroductions(List.of(
                            BulkCreatePartsCommand.PartData.builder()
                                    .part("서버")  // ✅ 한글
                                    .description("서버 파트")
                                    .build()
                    ))
                    .partCurriculums(List.of())
                    .build();

            partService.bulkCreate(command);

            // then
            List<PartDetailView> result = partService.findByGeneration(35);
            assertThat(result).hasSize(1);
            assertThat(result.get(0).part()).isEqualTo("서버");  // ✅ 한글
            // 기존 WEB 파트는 삭제됨
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("파트 조회")
    class Find {

        @Test
        @DisplayName("🔍 조회: 기수별 전체 조회 (파트 타입 순)")
        void findByGeneration_Ordered() {
            // given
            partRepository.saveAll(List.of(
                    createEntity(35, PartType.WEB),
                    createEntity(35, PartType.ANDROID),
                    createEntity(35, PartType.SERVER)
            ));

            // when
            List<PartDetailView> result = partService.findByGeneration(35);

            // then
            assertThat(result).hasSize(3);
            // PartType enum 순서대로 정렬
        }


        @Test
        @DisplayName("🔍 조회: 파트 소개 목록 (Main 페이지용)")
        void findIntroductionsByGeneration_Success() {
            // given
            partRepository.saveAll(List.of(
                    createEntity(35, PartType.ANDROID),
                    createEntity(35, PartType.IOS)
            ));

            // when
            List<PartIntroductionView> result = partService.findIntroductionsByGeneration(35);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(PartIntroductionView::part)
                    .containsExactlyInAnyOrder("안드로이드", "iOS");  // ✅ 한글
        }

        @Test
        @DisplayName("🔍 조회: 파트 커리큘럼 목록 (About 페이지용)")
        void findCurriculumsByGeneration_Success() {
            // given
            partRepository.save(Part.builder()
                    .generationId(35)
                    .partType(PartType.ANDROID)
                    .description("설명")
                    .curriculums(List.of("1주차", "2주차", "3주차"))
                    .build());

            // when
            List<PartCurriculumView> result = partService.findCurriculumsByGeneration(35);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).curriculums()).hasSize(3);
        }

    }

    // ===== Helper Methods =====

    private Part createEntity(Integer generationId, PartType partType) {
        return Part.builder()
                .generationId(generationId)
                .partType(partType)
                .description(partType.getValue() + " 파트입니다.")  // 한글: "안드로이드 파트입니다."
                .curriculums(List.of("1주차 기초", "2주차 심화"))
                .build();
    }
}
