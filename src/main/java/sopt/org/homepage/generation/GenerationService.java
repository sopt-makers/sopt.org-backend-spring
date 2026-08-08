package sopt.org.homepage.generation;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.generation.dto.CreateGenerationCommand;
import sopt.org.homepage.generation.dto.GenerationDetailView;
import sopt.org.homepage.generation.vo.BrandingColor;
import sopt.org.homepage.global.exception.ClientBadRequestException;

/**
 * GenerationService
 * <p>
 * 통합 Service (Command + Query) - 기수 관리 (SOPT 35기, 36기 등)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GenerationService {

    private final GenerationRepository generationRepository;

    // ===== Command 메서드 =====

    /**
     * 기수 생성
     */
    @Transactional
    public Integer create(CreateGenerationCommand command) {
        log.info("기수 생성 - id={}", command.id());

        // 중복 검증
        if (generationRepository.existsById(command.id())) {
            throw new ClientBadRequestException(
                    String.format("Generation %d already exists", command.id())
            );
        }

        Generation generation = command.toEntity();
        Generation saved = generationRepository.save(generation);

        log.info("기수 생성 완료 - id={}", saved.getId());
        return saved.getId();
    }

    /**
     * 공통 탭 배포: Generation 없으면 생성(이미지 공란), 있으면 공통 필드만 업데이트
     */
    @Transactional
    public void createOrUpdateCommon(Integer id, String name, BrandingColor brandingColor) {
        log.info("공통 탭 Generation createOrUpdate - id={}", id);

        Optional<Generation> opt = generationRepository.findById(id);
        if (opt.isEmpty()) {
            Generation gen = Generation.builder()
                    .id(id)
                    .name(name)
                    .headerImage("")
                    .recruitHeaderImage("")
                    .homeHeaderImage("")
                    .brandingColor(brandingColor)
                    .build();
            generationRepository.save(gen);
        } else {
            Generation gen = opt.get();
            gen.update(name, gen.getHeaderImage(), gen.getRecruitHeaderImage(),
                    gen.getHomeHeaderImage(), brandingColor);
        }
    }

    /**
     * 홈 탭 배포: homeHeaderImage 업데이트
     */
    @Transactional
    public void updateHomeHeaderImage(Integer id, String homeHeaderImage) {
        log.info("홈 탭 Generation homeHeaderImage 업데이트 - id={}", id);

        Generation gen = generationRepository.findById(id)
                .orElseThrow(() -> new ClientBadRequestException(
                        "공통 탭을 먼저 배포해야 합니다. Generation not found: " + id));
        gen.updateHomeHeaderImage(homeHeaderImage);
    }

    /**
     * 소개 탭 배포: headerImage 업데이트
     */
    @Transactional
    public void updateHeaderImage(Integer id, String headerImage) {
        log.info("소개 탭 Generation headerImage 업데이트 - id={}", id);

        Generation gen = generationRepository.findById(id)
                .orElseThrow(() -> new ClientBadRequestException(
                        "공통 탭을 먼저 배포해야 합니다. Generation not found: " + id));
        gen.updateHeaderImage(headerImage);
    }

    /**
     * 모집안내 탭 배포: recruitHeaderImage 업데이트
     */
    @Transactional
    public void updateRecruitHeaderImage(Integer id, String recruitHeaderImage) {
        log.info("모집안내 탭 Generation recruitHeaderImage 업데이트 - id={}", id);

        Generation gen = generationRepository.findById(id)
                .orElseThrow(() -> new ClientBadRequestException(
                        "공통 탭을 먼저 배포해야 합니다. Generation not found: " + id));
        gen.updateRecruitHeaderImage(recruitHeaderImage);
    }

    // ===== Query 메서드 =====

    /**
     * 특정 기수 상세 조회
     */
    @Transactional(readOnly = true)
    public GenerationDetailView findById(Integer generationId) {
        log.debug("기수 조회 - id={}", generationId);

        return generationRepository.findById(generationId)
                .map(GenerationDetailView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Generation %d not found", generationId)
                ));
    }

    /**
     * 최신 기수 조회
     */
    @Transactional(readOnly = true)
    public GenerationDetailView findLatest() {
        log.debug("최신 기수 조회");

        return generationRepository.findFirstByOrderByIdDesc()
                .map(GenerationDetailView::from)
                .orElseThrow(() -> new ClientBadRequestException("No generation found"));
    }

}
