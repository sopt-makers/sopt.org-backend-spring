package sopt.org.homepage.generation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.generation.dto.CreateGenerationCommand;
import sopt.org.homepage.generation.dto.GenerationDetailView;
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
