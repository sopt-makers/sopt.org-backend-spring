package sopt.org.homepage.part.service.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.part.domain.Part;
import sopt.org.homepage.part.repository.command.PartCommandRepository;
import sopt.org.homepage.part.repository.query.PartQueryRepository;
import sopt.org.homepage.part.service.command.dto.BulkCreatePartsCommand;
import sopt.org.homepage.part.service.command.dto.CreatePartCommand;
import sopt.org.homepage.part.service.command.dto.UpdatePartCommand;

/**
 * PartCommandService
 * <p>
 * 책임: PartType 엔티티의 생성, 수정, 삭제 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PartCommandService {

    private final PartCommandRepository partCommandRepository;
    private final PartQueryRepository partQueryRepository;

    /**
     * 파트 생성
     */
    public Long createPart(CreatePartCommand command) {
        log.info("Creating partType {} for generation: {}", command.partType(), command.generationId());

        // 중복 검증
        if (partQueryRepository.findByGenerationIdAndPartType(
                command.generationId(), command.partType()).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("PartType %s already exists for generation %d",
                            command.partType(), command.generationId())
            );
        }

        Part part = command.toEntity();
        Part saved = partCommandRepository.save(part);

        log.info("PartType created: {}", saved.getId());
        return saved.getId();
    }

    /**
     * 파트 일괄 생성 (기존 데이터 모두 삭제 후 재생성) Admin에서 기수별 파트 전체 교체 시 사용
     * <p>
     * PartIntroduction + PartCurriculum을 병합하여 PartType 생성
     */
    public List<Long> bulkCreateParts(BulkCreatePartsCommand command) {
        log.info("Bulk creating parts for generation: {}", command.generationId());

        // 1. 기존 파트 모두 삭제
        partCommandRepository.deleteByGenerationId(command.generationId());

        // 2. PartIntroduction과 PartCurriculum을 파트별로 병합
        Map<PartType, PartMergeData> partDataMap = new HashMap<>();

        // 2-1. PartIntroduction 데이터 수집
        for (BulkCreatePartsCommand.PartData intro : command.partIntroductions()) {
            PartType partType = PartType.fromString(intro.part());
            partDataMap.computeIfAbsent(partType, k -> new PartMergeData())
                    .setDescription(intro.description());
        }

        // 2-2. PartCurriculum 데이터 수집
        for (BulkCreatePartsCommand.PartCurriculumData curriculum : command.partCurriculums()) {
            PartType partType = PartType.fromString(curriculum.part());
            partDataMap.computeIfAbsent(partType, k -> new PartMergeData())
                    .setCurriculums(curriculum.curriculums());
        }

        // 3. 병합된 데이터로 PartType 엔티티 생성
        List<Part> parts = new ArrayList<>();
        for (Map.Entry<PartType, PartMergeData> entry : partDataMap.entrySet()) {
            PartType partType = entry.getKey();
            PartMergeData data = entry.getValue();

            // description이나 curriculums가 없으면 기본값 설정
            String description = data.getDescription() != null
                    ? data.getDescription()
                    : partType.getValue() + " 파트입니다.";

            List<String> curriculums = data.getCurriculums() != null
                    ? data.getCurriculums()
                    : new ArrayList<>();

            Part part = Part.builder()
                    .generationId(command.generationId())
                    .partType(partType)
                    .description(description)
                    .curriculums(curriculums)
                    .build();

            parts.add(part);
        }

        List<Part> saved = partCommandRepository.saveAll(parts);

        log.info("Bulk created {} parts for generation: {}", saved.size(), command.generationId());

        return saved.stream().map(Part::getId).toList();
    }

    /**
     * 파트 정보 수정
     */
    public void updatePart(UpdatePartCommand command) {
        log.info("Updating partType: {}", command.id());

        Part part = partQueryRepository.findById(command.id())
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("PartType %d not found", command.id())
                ));

        part.update(command.description(), command.curriculums());

        log.info("PartType updated: {}", command.id());
    }

    /**
     * 파트 소개글만 수정
     */
    public void updateDescription(Long partId, String description) {
        log.info("Updating partType description: {}", partId);

        Part part = partQueryRepository.findById(partId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("PartType %d not found", partId)
                ));

        part.updateDescription(description);

        log.info("PartType description updated: {}", partId);
    }

    /**
     * 파트 커리큘럼만 수정
     */
    public void updateCurriculums(Long partId, List<String> curriculums) {
        log.info("Updating partType curriculums: {}", partId);

        Part part = partQueryRepository.findById(partId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("PartType %d not found", partId)
                ));

        part.updateCurriculums(curriculums);

        log.info("PartType curriculums updated: {}", partId);
    }

    /**
     * 특정 주차 커리큘럼 수정
     */
    public void updateCurriculum(Long partId, int week, String curriculum) {
        log.info("Updating partType curriculum week {}: {}", week, partId);

        Part part = partQueryRepository.findById(partId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("PartType %d not found", partId)
                ));

        part.updateCurriculum(week, curriculum);

        log.info("PartType curriculum updated: week {}", week);
    }

    /**
     * 커리큘럼 추가
     */
    public void addCurriculum(Long partId, String curriculum) {
        log.info("Adding curriculum to partType: {}", partId);

        Part part = partQueryRepository.findById(partId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("PartType %d not found", partId)
                ));

        part.addCurriculum(curriculum);

        log.info("Curriculum added to partType: {}", partId);
    }

    /**
     * 파트 삭제
     */
    public void deletePart(Long partId) {
        log.info("Deleting partType: {}", partId);

        if (!partQueryRepository.findById(partId).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("PartType %d not found", partId)
            );
        }

        partCommandRepository.deleteById(partId);

        log.info("PartType deleted: {}", partId);
    }

    /**
     * 특정 기수의 모든 파트 삭제
     */
    public void deleteAllByGeneration(Integer generationId) {
        log.info("Deleting all parts for generation: {}", generationId);

        partCommandRepository.deleteByGenerationId(generationId);

        log.info("All parts deleted for generation: {}", generationId);
    }

    // === 내부 클래스 ===

    /**
     * PartIntroduction + PartCurriculum 병합용 데이터 클래스
     */
    private static class PartMergeData {
        private String description;
        private List<String> curriculums;

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<String> getCurriculums() {
            return curriculums;
        }

        public void setCurriculums(List<String> curriculums) {
            this.curriculums = curriculums;
        }
    }
}
