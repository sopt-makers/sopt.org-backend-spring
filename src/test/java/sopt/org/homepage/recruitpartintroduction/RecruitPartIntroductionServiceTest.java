package sopt.org.homepage.recruitpartintroduction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.recruitpartintroduction.dto.BulkCreateRecruitPartIntroductionsCommand;
import sopt.org.homepage.recruitpartintroduction.dto.RecruitPartIntroductionView;
import sopt.org.homepage.recruitpartintroduction.vo.PartIntroduction;

/**
 * RecruitPartIntroduction 통합 테스트
 * <p>
 * 인수인계 목적: - RecruitPartIntroduction은 모집 시 파트별 소개를 나타냄 - 기수(generationId)별, 파트(PartType)별로 관리됨 - PartIntroduction VO로
 * content, preference 관리
 */
@DisplayName("RecruitPartIntroduction 서비스 통합 테스트")
class RecruitPartIntroductionServiceTest extends IntegrationTestBase {

    @Autowired
    private RecruitPartIntroductionService service;

    @Autowired
    private RecruitPartIntroductionRepository repository;

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    // ===== 생성 시나리오 =====

    @Nested
    @DisplayName("파트 소개 생성")
    class Create {

        @Test
        @DisplayName("✅ 정상: 일괄 생성 (기존 데이터 삭제)")
        void bulkCreate_ReplacesExisting() {
            // given - 기존 데이터
            repository.save(createEntity(35, PartType.WEB));
            assertThat(repository.countByGenerationId(35)).isEqualTo(1);

            // when
            BulkCreateRecruitPartIntroductionsCommand command = BulkCreateRecruitPartIntroductionsCommand.builder()
                    .generationId(35)
                    .partIntroductions(List.of(
                            BulkCreateRecruitPartIntroductionsCommand.PartIntroductionData.builder()
                                    .part("안드로이드")
                                    .introduction(BulkCreateRecruitPartIntroductionsCommand.IntroductionData.builder()
                                            .content("안드로이드 소개")
                                            .preference("Kotlin 선호")
                                            .build())
                                    .build(),
                            BulkCreateRecruitPartIntroductionsCommand.PartIntroductionData.builder()
                                    .part("iOS")
                                    .introduction(BulkCreateRecruitPartIntroductionsCommand.IntroductionData.builder()
                                            .content("iOS 소개")
                                            .preference("Swift 선호")
                                            .build())
                                    .build()
                    ))
                    .build();

            List<Long> ids = service.bulkCreate(command);

            // then
            assertThat(ids).hasSize(2);

            List<RecruitPartIntroductionView> result = service.findByGeneration(35);
            assertThat(result).hasSize(2);
            // 기존 WEB 파트는 삭제됨
            assertThat(result).extracting(RecruitPartIntroductionView::part)
                    .containsExactlyInAnyOrder("안드로이드", "iOS");
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("파트 소개 조회")
    class Find {

        @Test
        @DisplayName("🔍 조회: 기수별 전체 조회 (파트 순)")
        void findByGeneration_Ordered() {
            // given
            repository.saveAll(List.of(
                    createEntity(35, PartType.WEB),
                    createEntity(35, PartType.ANDROID),
                    createEntity(35, PartType.SERVER)
            ));

            // when
            List<RecruitPartIntroductionView> result = service.findByGeneration(35);

            // then
            assertThat(result).hasSize(3);
        }


    }

    // ===== VO 검증 시나리오 =====

    @Nested
    @DisplayName("Value Object 검증")
    class VOValidation {

        @Test
        @DisplayName("📌 PartIntroduction: 빈 content")
        void partIntroduction_BlankContent_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> PartIntroduction.builder()
                    .content("")
                    .preference("선호사항")
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("content must not be blank");
        }

        @Test
        @DisplayName("📌 PartIntroduction: 빈 preference")
        void partIntroduction_BlankPreference_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> PartIntroduction.builder()
                    .content("소개")
                    .preference("")
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("preference must not be blank");
        }

        @Test
        @DisplayName("📌 PartIntroduction: content 수정 (불변성)")
        void partIntroduction_UpdateContent_Immutable() {
            // given
            PartIntroduction original = PartIntroduction.builder()
                    .content("원래 소개")
                    .preference("원래 선호")
                    .build();

            // when
            PartIntroduction updated = original.updateContent("새 소개");

            // then
            assertThat(updated).isNotSameAs(original);  // 새 객체
            assertThat(updated.getContent()).isEqualTo("새 소개");
            assertThat(updated.getPreference()).isEqualTo("원래 선호");  // 유지
            assertThat(original.getContent()).isEqualTo("원래 소개");  // 원본 불변
        }
    }

    // ===== Helper Methods =====


    private RecruitPartIntroduction createEntity(Integer generationId, PartType part) {
        return RecruitPartIntroduction.builder()
                .generationId(generationId)
                .part(part)
                .introduction(PartIntroduction.builder()
                        .content(part.getValue() + " 파트 소개입니다.")
                        .preference("관련 경험자 우대")
                        .build())
                .build();
    }
}
