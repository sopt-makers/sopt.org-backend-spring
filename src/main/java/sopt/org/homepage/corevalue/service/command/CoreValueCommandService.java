package sopt.org.homepage.corevalue.service.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.corevalue.domain.CoreValue;
import sopt.org.homepage.corevalue.repository.command.CoreValueCommandRepository;
import sopt.org.homepage.corevalue.repository.query.CoreValueQueryRepository;
import sopt.org.homepage.corevalue.service.command.dto.BulkCreateCoreValuesCommand;
import sopt.org.homepage.corevalue.service.command.dto.CreateCoreValueCommand;
import sopt.org.homepage.corevalue.service.command.dto.UpdateCoreValueCommand;
import sopt.org.homepage.global.exception.ClientBadRequestException;

/**
 * CoreValueCommandService
 * <p>
 * 책임: CoreValue 엔티티의 생성, 수정, 삭제 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CoreValueCommandService {

    private final CoreValueCommandRepository coreValueCommandRepository;
    private final CoreValueQueryRepository coreValueQueryRepository;

    /**
     * 핵심 가치 생성
     */
    public Long createCoreValue(CreateCoreValueCommand command) {
        log.info("Creating core value for generation: {}", command.generationId());

        CoreValue coreValue = command.toEntity();
        CoreValue saved = coreValueCommandRepository.save(coreValue);

        log.info("Core value created: {}", saved.getId());
        return saved.getId();
    }

    /**
     * 핵심 가치 일괄 생성 (기존 데이터 모두 삭제 후 재생성) Admin에서 기수별 핵심 가치 전체 교체 시 사용
     */
    public List<Long> bulkCreateCoreValues(BulkCreateCoreValuesCommand command) {
        log.info("Bulk creating core values for generation: {}", command.generationId());

        // 1. 기존 핵심 가치 모두 삭제
        coreValueCommandRepository.deleteByGenerationId(command.generationId());

        // 2. 새로운 핵심 가치 생성
        List<CoreValue> coreValues = command.coreValues().stream()
                .map(data -> CoreValue.builder()
                        .generationId(command.generationId())
                        .value(data.value())
                        .description(data.description())
                        .imageUrl(data.imageUrl())
                        .displayOrder(data.displayOrder())
                        .build())
                .toList();

        List<CoreValue> saved = coreValueCommandRepository.saveAll(coreValues);

        log.info("Bulk created {} core values for generation: {}",
                saved.size(), command.generationId());

        return saved.stream().map(CoreValue::getId).toList();
    }

    /**
     * 핵심 가치 수정
     */
    public void updateCoreValue(UpdateCoreValueCommand command) {
        log.info("Updating core value: {}", command.id());

        CoreValue coreValue = coreValueQueryRepository.findById(command.id())
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("CoreValue %d not found", command.id())
                ));

        coreValue.update(
                command.value(),
                command.description(),
                command.imageUrl(),
                command.displayOrder()
        );

        log.info("Core value updated: {}", command.id());
    }

    /**
     * 핵심 가치 이미지만 수정
     */
    public void updateCoreValueImage(Long coreValueId, String imageUrl) {
        log.info("Updating core value image: {}", coreValueId);

        CoreValue coreValue = coreValueQueryRepository.findById(coreValueId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("CoreValue %d not found", coreValueId)
                ));

        coreValue.updateImage(imageUrl);

        log.info("Core value image updated: {}", coreValueId);
    }

    /**
     * 핵심 가치 순서 변경
     */
    public void updateDisplayOrder(Long coreValueId, Integer displayOrder) {
        log.info("Updating core value display order: {} to {}", coreValueId, displayOrder);

        CoreValue coreValue = coreValueQueryRepository.findById(coreValueId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("CoreValue %d not found", coreValueId)
                ));

        coreValue.updateDisplayOrder(displayOrder);

        log.info("Core value display order updated: {}", coreValueId);
    }

    /**
     * 핵심 가치 삭제
     */
    public void deleteCoreValue(Long coreValueId) {
        log.info("Deleting core value: {}", coreValueId);

        if (!coreValueQueryRepository.findById(coreValueId).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("CoreValue %d not found", coreValueId)
            );
        }

        coreValueCommandRepository.deleteById(coreValueId);

        log.info("Core value deleted: {}", coreValueId);
    }

    /**
     * 특정 기수의 모든 핵심 가치 삭제
     */
    public void deleteAllByGeneration(Integer generationId) {
        log.info("Deleting all core values for generation: {}", generationId);

        coreValueCommandRepository.deleteByGenerationId(generationId);

        log.info("All core values deleted for generation: {}", generationId);
    }
}
