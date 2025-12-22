package sopt.org.homepage.part;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.part.dto.BulkCreatePartsCommand;
import sopt.org.homepage.part.dto.PartCurriculumView;
import sopt.org.homepage.part.dto.PartDetailView;
import sopt.org.homepage.part.dto.PartIntroductionView;

/**
 * PartService
 * <p>
 * 통합 Service (Command + Query) - SOPT 파트 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PartService {

    private final PartRepository partRepository;

    // ===== Command 메서드 =====

    /**
     * 파트 일괄 생성 (기존 데이터 삭제 후 재생성) Admin에서 기수별 파트 전체 교체 시 사용
     * <p>
     * PartIntroduction + PartCurriculum을 병합하여 Part 생성
     */
    @Transactional
    public List<Long> bulkCreate(BulkCreatePartsCommand command) {
        log.info("파트 일괄 생성 - generationId={}", command.generationId());

        // 1. 기존 파트 모두 삭제
        partRepository.deleteByGenerationId(command.generationId());

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

        // 3. 병합된 데이터로 Part 엔티티 생성
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

        List<Part> saved = partRepository.saveAll(parts);

        log.info("파트 일괄 생성 완료 - count={}", saved.size());
        return saved.stream().map(Part::getId).toList();
    }

    // ===== Query 메서드 =====

    /**
     * 특정 기수의 모든 파트 조회 (상세)
     */
    @Transactional(readOnly = true)
    public List<PartDetailView> findByGeneration(Integer generationId) {
        log.debug("기수별 파트 조회 - generationId={}", generationId);

        return partRepository.findByGenerationIdOrderByPartTypeAsc(generationId)
                .stream()
                .map(PartDetailView::from)
                .toList();
    }


    /**
     * 특정 기수의 모든 파트 소개 조회 (Main 페이지용)
     */
    @Transactional(readOnly = true)
    public List<PartIntroductionView> findIntroductionsByGeneration(Integer generationId) {
        log.debug("기수별 파트 소개 조회 - generationId={}", generationId);

        return partRepository.findByGenerationIdOrderByPartTypeAsc(generationId)
                .stream()
                .map(PartIntroductionView::from)
                .toList();
    }

    /**
     * 특정 기수의 모든 파트 커리큘럼 조회 (About 페이지용)
     */
    @Transactional(readOnly = true)
    public List<PartCurriculumView> findCurriculumsByGeneration(Integer generationId) {
        log.debug("기수별 파트 커리큘럼 조회 - generationId={}", generationId);

        return partRepository.findByGenerationIdOrderByPartTypeAsc(generationId)
                .stream()
                .map(PartCurriculumView::from)
                .toList();
    }

    // ===== 내부 클래스 =====

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
