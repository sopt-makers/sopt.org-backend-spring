package sopt.org.homepage.recruitpartintroduction;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.common.type.PartType;
import sopt.org.homepage.recruitpartintroduction.dto.BulkCreateRecruitPartIntroductionsCommand;
import sopt.org.homepage.recruitpartintroduction.dto.RecruitPartIntroductionView;
import sopt.org.homepage.recruitpartintroduction.vo.PartIntroduction;

/**
 * RecruitPartIntroductionService
 * <p>
 * 통합 Service (Command + Query) - 모집 시 파트별 소개 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitPartIntroductionService {

    private final RecruitPartIntroductionRepository repository;

    // ===== Command 메서드 =====

    /**
     * 파트 소개 일괄 생성 (기존 데이터 삭제 후 재생성) Admin에서 기수별 파트 소개 전체 교체 시 사용
     */
    @Transactional
    public List<Long> bulkCreate(BulkCreateRecruitPartIntroductionsCommand command) {
        log.info("파트 소개 일괄 생성 - generationId={}", command.generationId());

        // 1. 기존 파트 소개 모두 삭제
        repository.deleteByGenerationId(command.generationId());

        // 2. 새로운 파트 소개 생성
        List<RecruitPartIntroduction> entities = command.partIntroductions().stream()
                .map(data -> RecruitPartIntroduction.builder()
                        .generationId(command.generationId())
                        .part(PartType.fromString(data.part()))
                        .introduction(PartIntroduction.builder()
                                .content(data.introduction().content())
                                .preference(data.introduction().preference())
                                .build())
                        .build())
                .toList();

        List<RecruitPartIntroduction> saved = repository.saveAll(entities);

        log.info("파트 소개 일괄 생성 완료 - count={}", saved.size());
        return saved.stream().map(RecruitPartIntroduction::getId).toList();
    }

    // ===== Query 메서드 =====

    /**
     * 특정 기수의 모든 파트 소개 조회
     */
    @Transactional(readOnly = true)
    public List<RecruitPartIntroductionView> findByGeneration(Integer generationId) {
        log.debug("기수별 파트 소개 조회 - generationId={}", generationId);

        return repository.findByGenerationIdOrderByPartAsc(generationId)
                .stream()
                .map(RecruitPartIntroductionView::from)
                .toList();
    }


}
