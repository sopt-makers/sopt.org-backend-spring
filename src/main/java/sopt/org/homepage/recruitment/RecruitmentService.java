package sopt.org.homepage.recruitment;

import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.recruitment.dto.BulkCreateRecruitmentsCommand;
import sopt.org.homepage.recruitment.dto.RecruitmentView;
import sopt.org.homepage.recruitment.vo.RecruitType;
import sopt.org.homepage.recruitment.vo.Schedule;

/**
 * RecruitmentService
 * <p>
 * 통합 Service (Command + Query) - 기수별 모집 일정 관리 (OB/YB)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final RecruitmentRepository recruitmentRepository;

    // ===== Command 메서드 =====

    /**
     * 모집 일정 일괄 생성 (기존 데이터 삭제 후 재생성) Admin에서 기수별 모집 일정 전체 교체 시 사용
     */
    @Transactional
    public List<Long> bulkCreate(BulkCreateRecruitmentsCommand command) {
        log.info("모집 일정 일괄 생성 - generationId={}", command.generationId());

        // 1. 기존 모집 일정 모두 삭제
        recruitmentRepository.deleteByGenerationId(command.generationId());

        // 2. 새로운 모집 일정 생성
        List<Recruitment> recruitments = command.recruitments().stream()
                .map(data -> Recruitment.builder()
                        .generationId(command.generationId())
                        .recruitType(RecruitType.fromString(data.type()))
                        .schedule(Schedule.builder()
                                .applicationStartTime(data.schedule().applicationStartTime())
                                .applicationEndTime(data.schedule().applicationEndTime())
                                .applicationResultTime(data.schedule().applicationResultTime())
                                .interviewStartTime(data.schedule().interviewStartTime())
                                .interviewEndTime(data.schedule().interviewEndTime())
                                .finalResultTime(data.schedule().finalResultTime())
                                .build())
                        .build())
                .toList();

        List<Recruitment> saved = recruitmentRepository.saveAll(recruitments);

        log.info("모집 일정 일괄 생성 완료 - count={}", saved.size());
        return saved.stream().map(Recruitment::getId).toList();
    }

    // ===== Query 메서드 =====

    /**
     * 특정 모집 일정 상세 조회
     */
    @Transactional(readOnly = true)
    public RecruitmentView findById(Long recruitmentId) {
        log.debug("모집 일정 조회 - id={}", recruitmentId);

        return recruitmentRepository.findById(recruitmentId)
                .map(RecruitmentView::from)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Recruitment %d not found", recruitmentId)
                ));
    }

    /**
     * 특정 기수의 모든 모집 일정 조회
     */
    @Transactional(readOnly = true)
    public List<RecruitmentView> findByGeneration(Integer generationId) {
        log.debug("기수별 모집 일정 조회 - generationId={}", generationId);

        return recruitmentRepository.findByGenerationIdOrderByRecruitTypeAsc(generationId)
                .stream()
                .map(RecruitmentView::from)
                .toList();
    }


}
