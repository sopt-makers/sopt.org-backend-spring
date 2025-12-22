package sopt.org.homepage.corevalue;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.corevalue.dto.BulkCreateCoreValuesCommand;
import sopt.org.homepage.corevalue.dto.CoreValueView;
import sopt.org.homepage.corevalue.dto.CreateCoreValueCommand;
import sopt.org.homepage.global.exception.ClientBadRequestException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoreValueService {

    private final CoreValueRepository coreValueRepository;

    // ===== Command 메서드 =====

    @Transactional
    public Long create(CreateCoreValueCommand command) {
        log.info("핵심 가치 생성 - generationId={}", command.generationId());
        CoreValue coreValue = command.toEntity();
        CoreValue saved = coreValueRepository.save(coreValue);
        log.info("핵심 가치 생성 완료 - id={}", saved.getId());
        return saved.getId();
    }

    @Transactional
    public List<Long> bulkCreate(BulkCreateCoreValuesCommand command) {
        log.info("핵심 가치 일괄 생성 - generationId={}", command.generationId());

        // 기존 삭제 후 재생성
        coreValueRepository.deleteByGenerationId(command.generationId());

        List<CoreValue> coreValues = command.coreValues().stream()
                .map(data -> CoreValue.builder()
                        .generationId(command.generationId())
                        .value(data.value())
                        .description(data.description())
                        .imageUrl(data.imageUrl())
                        .displayOrder(data.displayOrder())
                        .build())
                .toList();

        List<CoreValue> saved = coreValueRepository.saveAll(coreValues);
        log.info("핵심 가치 일괄 생성 완료 - count={}", saved.size());

        return saved.stream().map(CoreValue::getId).toList();
    }

    // ===== Query 메서드 =====

    @Transactional(readOnly = true)
    public List<CoreValueView> findByGeneration(Integer generationId) {
        return coreValueRepository.findByGenerationIdOrderByDisplayOrderAsc(generationId)
                .stream()
                .map(CoreValueView::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CoreValueView findById(Long id) {
        return coreValueRepository.findById(id)
                .map(CoreValueView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("CoreValue %d not found", id)
                ));
    }

}
