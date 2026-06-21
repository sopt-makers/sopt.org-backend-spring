package sopt.org.homepage.generation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sopt.org.homepage.common.IntegrationTestBase;
import sopt.org.homepage.generation.dto.CreateGenerationCommand;
import sopt.org.homepage.generation.dto.GenerationDetailView;
import sopt.org.homepage.generation.vo.BrandingColor;
import sopt.org.homepage.generation.vo.MainButton;
import sopt.org.homepage.global.exception.ClientBadRequestException;

/**
 * Generation 통합 테스트
 * <p>
 * 인수인계 목적: - Generation은 SOPT 기수를 나타냄 (35기, 36기 등) - PK(id)는 자동 생성이 아닌 기수 번호를 직접 사용 - BrandingColor, MainButton은 VO로
 * 관리
 */
@DisplayName("Generation 서비스 통합 테스트")
class GenerationServiceTest extends IntegrationTestBase {

    @Autowired
    private GenerationService generationService;

    @Autowired
    private GenerationRepository generationRepository;

    @AfterEach
    void tearDown() {
        generationRepository.deleteAll();
    }

    // ===== 생성 시나리오 =====

    @Nested
    @DisplayName("기수 생성")
    class Create {

        @Test
        @DisplayName("✅ 정상: 새 기수 생성")
        void create_Success() {
            // given
            CreateGenerationCommand command = createCommand(35);

            // when
            Integer id = generationService.create(command);

            // then
            assertThat(id).isEqualTo(35);

            GenerationDetailView saved = generationService.findById(id);
            assertThat(saved.name()).isEqualTo("SOPT 35기");
            assertThat(saved.brandingColor().darkModeKeyColor()).isEqualTo("FF6B35");
            assertThat(saved.mainButton().text()).isEqualTo("지원하기");
        }

        @Test
        @DisplayName("❌ 실패: 중복 기수 생성 불가")
        void create_DuplicateId_ThrowsException() {
            // given
            generationRepository.save(createEntity(35));

            CreateGenerationCommand command = createCommand(35);

            // when & then
            assertThatThrownBy(() -> generationService.create(command))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("already exists");
        }
    }

    // ===== 조회 시나리오 =====

    @Nested
    @DisplayName("기수 조회")
    class Find {

        @Test
        @DisplayName("🔍 조회: 특정 기수 조회")
        void findById_Success() {
            // given
            generationRepository.save(createEntity(35));

            // when
            GenerationDetailView result = generationService.findById(35);

            // then
            assertThat(result.id()).isEqualTo(35);
            assertThat(result.name()).isEqualTo("SOPT 35기");
        }

        @Test
        @DisplayName("🔍 조회: 최신 기수 조회")
        void findLatest_Success() {
            // given
            generationRepository.saveAll(List.of(
                    createEntity(33),
                    createEntity(35),
                    createEntity(34)
            ));

            // when
            GenerationDetailView result = generationService.findLatest();

            // then
            assertThat(result.id()).isEqualTo(35);  // 가장 높은 번호
        }


        @Test
        @DisplayName("❌ 조회: 존재하지 않는 기수")
        void findById_NotFound_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> generationService.findById(999))
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("❌ 조회: 기수가 하나도 없을 때 최신 조회")
        void findLatest_Empty_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> generationService.findLatest())
                    .isInstanceOf(ClientBadRequestException.class)
                    .hasMessageContaining("No generation found");
        }
    }

    // ===== VO 검증 시나리오 =====

    @Nested
    @DisplayName("Value Object 검증")
    class VOValidation {

        @Test
        @DisplayName("📌 BrandingColor: 잘못된 Hex 컬러 코드")
        void brandingColor_InvalidHex_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> BrandingColor.builder()
                    .darkModeKeyColor("GGGGGG")  // 잘못된 Hex
                    .darkModeTextColor("white")
                    .lightModeKeyColor("111111")
                    .lightModeTextColor("black")
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("hex color");
        }

        @Test
        @DisplayName("📌 BrandingColor: 잘못된 텍스트 컬러")
        void brandingColor_InvalidTextColor_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> BrandingColor.builder()
                    .darkModeKeyColor("FF0000")
                    .darkModeTextColor("red")  // white 또는 black만 허용
                    .lightModeKeyColor("FF0000")
                    .lightModeTextColor("black")
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("white");
        }

        @Test
        @DisplayName("📌 MainButton: 빈 텍스트")
        void mainButton_BlankText_ThrowsException() {
            // when & then
            assertThatThrownBy(() -> MainButton.builder()
                    .text("")
                    .keyColor("#FF0000")
                    .subColor("#00FF00")
                    .build())
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("blank");
        }
    }

    // ===== Helper Methods =====

    private CreateGenerationCommand createCommand(Integer id) {
        return CreateGenerationCommand.builder()
                .id(id)
                .name("SOPT " + id + "기")
                .headerImage("https://example.com/header-" + id + ".jpg")
                .recruitHeaderImage("https://example.com/recruit-header-" + id + ".jpg")
                .homeHeaderImage("https://example.com/home-header-" + id + ".jpg")
                .brandingColor(CreateGenerationCommand.BrandingColorCommand.builder()
                        .darkModeKeyColor("FF6B35")
                        .darkModeTextColor("white")
                        .lightModeKeyColor("FF6B35")
                        .lightModeTextColor("black")
                        .build())
                .mainButton(CreateGenerationCommand.MainButtonCommand.builder()
                        .text("지원하기")
                        .keyColor("#FF6B35")
                        .subColor("#FFFFFF")
                        .build())
                .build();
    }

    private Generation createEntity(Integer id) {
        return Generation.builder()
                .id(id)
                .name("SOPT " + id + "기")
                .headerImage("https://example.com/header-" + id + ".jpg")
                .recruitHeaderImage("https://example.com/recruit-header-" + id + ".jpg")
                .homeHeaderImage("https://example.com/home-header-" + id + ".jpg")
                .brandingColor(BrandingColor.builder()
                        .darkModeKeyColor("FF6B35")
                        .darkModeTextColor("white")
                        .lightModeKeyColor("FF6B35")
                        .lightModeTextColor("black")
                        .build())
                .mainButton(MainButton.builder()
                        .text("지원하기")
                        .keyColor("#FF6B35")
                        .subColor("#FFFFFF")
                        .build())
                .build();
    }
}
