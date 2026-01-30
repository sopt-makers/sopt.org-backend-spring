package sopt.org.homepage.corevalue;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.corevalue.dto.BulkCreateCoreValuesCommand;
import sopt.org.homepage.corevalue.dto.CoreValueView;
import sopt.org.homepage.corevalue.dto.CreateCoreValueCommand;

@DisplayName("CoreValue 서비스 통합 테스트")
class CoreValueServiceTest extends IntegrationTestBase {

    @Autowired
    private CoreValueService coreValueService;

    @Autowired
    private CoreValueRepository coreValueRepository;

    @AfterEach
    void tearDown() {
        coreValueRepository.deleteAll();
    }

    @Nested
    @DisplayName("생성")
    class Create {
        @Test
        @DisplayName("✅ 단일 핵심 가치 생성")
        void create_Success() {
            // given
            CreateCoreValueCommand command = CreateCoreValueCommand.builder()
                    .generationId(35)
                    .value("도전")
                    .description("새로운 도전을 두려워하지 않습니다")
                    .imageUrl("https://example.com/challenge.jpg")
                    .displayOrder(1)
                    .build();

            // when
            Long id = coreValueService.create(command);

            // then
            assertThat(id).isNotNull();
            CoreValueView saved = coreValueService.findById(id);
            assertThat(saved.value()).isEqualTo("도전");
        }

        @Test
        @DisplayName("✅ 일괄 생성 (기존 데이터 교체)")
        void bulkCreate_ReplacesExisting() {
            // given - 기존 데이터
            coreValueRepository.save(CoreValue.builder()
                    .generationId(35).value("기존값").description("d")
                    .imageUrl("u").displayOrder(1).build());

            // when - 교체
            BulkCreateCoreValuesCommand command = BulkCreateCoreValuesCommand.builder()
                    .generationId(35)
                    .coreValues(List.of(
                            BulkCreateCoreValuesCommand.CoreValueData.builder()
                                    .value("도전").description("d1").imageUrl("u1").displayOrder(1).build(),
                            BulkCreateCoreValuesCommand.CoreValueData.builder()
                                    .value("성장").description("d2").imageUrl("u2").displayOrder(2).build()
                    ))
                    .build();

            List<Long> ids = coreValueService.bulkCreate(command);

            // then
            assertThat(ids).hasSize(2);
            List<CoreValueView> result = coreValueService.findByGeneration(35);
            assertThat(result).extracting(CoreValueView::value)
                    .containsExactly("도전", "성장");
        }
    }

    @Nested
    @DisplayName("조회")
    class Find {
        @Test
        @DisplayName("🔍 기수별 조회 (순서대로)")
        void findByGeneration_Ordered() {
            // given
            coreValueRepository.saveAll(List.of(
                    CoreValue.builder().generationId(35).value("세번째").description("d").imageUrl("u").displayOrder(3)
                            .build(),
                    CoreValue.builder().generationId(35).value("첫번째").description("d").imageUrl("u").displayOrder(1)
                            .build(),
                    CoreValue.builder().generationId(35).value("두번째").description("d").imageUrl("u").displayOrder(2)
                            .build()
            ));

            // when
            List<CoreValueView> result = coreValueService.findByGeneration(35);

            // then
            assertThat(result).extracting(CoreValueView::value)
                    .containsExactly("첫번째", "두번째", "세번째");
        }
    }
}
