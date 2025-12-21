package sopt.org.homepage.recruitment.service.command;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.global.exception.ClientBadRequestException;
import sopt.org.homepage.recruitment.domain.Recruitment;
import sopt.org.homepage.recruitment.domain.vo.RecruitType;
import sopt.org.homepage.recruitment.domain.vo.Schedule;
import sopt.org.homepage.recruitment.repository.command.RecruitmentCommandRepository;
import sopt.org.homepage.recruitment.repository.query.RecruitmentQueryRepository;
import sopt.org.homepage.recruitment.service.command.dto.BulkCreateRecruitmentsCommand;
import sopt.org.homepage.recruitment.service.command.dto.CreateRecruitmentCommand;
import sopt.org.homepage.recruitment.service.command.dto.UpdateRecruitmentCommand;

/**
 * RecruitmentCommandService
 * <p>
 * 책임: Recruitment 엔티티의 생성, 수정, 삭제 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecruitmentCommandService {

    private final RecruitmentCommandRepository recruitmentCommandRepository;
    private final RecruitmentQueryRepository recruitmentQueryRepository;

    /**
     * 모집 일정 생성
     */
    public Long createRecruitment(CreateRecruitmentCommand command) {
        log.info("Creating recruitment {} for generation: {}",
                command.recruitType(), command.generationId());

        // 중복 검증
        if (recruitmentQueryRepository.findByGenerationIdAndRecruitType(
                command.generationId(), command.recruitType()).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("Recruitment %s already exists for generation %d",
                            command.recruitType(), command.generationId())
            );
        }

        Recruitment recruitment = command.toEntity();
        Recruitment saved = recruitmentCommandRepository.save(recruitment);

        log.info("Recruitment created: {}", saved.getId());
        return saved.getId();
    }

    /**
     * 모집 일정 일괄 생성 (기존 데이터 모두 삭제 후 재생성)
     */
    public List<Long> bulkCreateRecruitments(BulkCreateRecruitmentsCommand command) {
        log.info("Bulk creating recruitments for generation: {}", command.generationId());

        // 1. 기존 모집 일정 모두 삭제
        recruitmentCommandRepository.deleteByGenerationId(command.generationId());

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

        List<Recruitment> saved = recruitmentCommandRepository.saveAll(recruitments);

        log.info("Bulk created {} recruitments for generation: {}",
                saved.size(), command.generationId());

        return saved.stream().map(Recruitment::getId).toList();
    }

    /**
     * 모집 일정 수정
     */
    public void updateRecruitment(UpdateRecruitmentCommand command) {
        log.info("Updating recruitment: {}", command.id());

        Recruitment recruitment = recruitmentQueryRepository.findById(command.id())
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Recruitment %d not found", command.id())
                ));

        recruitment.updateSchedule(command.schedule().toVO());

        log.info("Recruitment updated: {}", command.id());
    }

    /**
     * 지원 기간만 수정
     */
    public void updateApplicationPeriod(Long recruitmentId, String startTime, String endTime) {
        log.info("Updating application period for recruitment: {}", recruitmentId);

        Recruitment recruitment = recruitmentQueryRepository.findById(recruitmentId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Recruitment %d not found", recruitmentId)
                ));

        recruitment.updateApplicationPeriod(startTime, endTime);

        log.info("Application period updated for recruitment: {}", recruitmentId);
    }

    /**
     * 면접 기간만 수정
     */
    public void updateInterviewPeriod(Long recruitmentId, String startTime, String endTime) {
        log.info("Updating interview period for recruitment: {}", recruitmentId);

        Recruitment recruitment = recruitmentQueryRepository.findById(recruitmentId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Recruitment %d not found", recruitmentId)
                ));

        recruitment.updateInterviewPeriod(startTime, endTime);

        log.info("Interview period updated for recruitment: {}", recruitmentId);
    }

    /**
     * 모집 일정 삭제
     */
    public void deleteRecruitment(Long recruitmentId) {
        log.info("Deleting recruitment: {}", recruitmentId);

        if (!recruitmentQueryRepository.findById(recruitmentId).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("Recruitment %d not found", recruitmentId)
            );
        }

        recruitmentCommandRepository.deleteById(recruitmentId);

        log.info("Recruitment deleted: {}", recruitmentId);
    }

    /**
     * 특정 기수의 모든 모집 일정 삭제
     */
    public void deleteAllByGeneration(Integer generationId) {
        log.info("Deleting all recruitments for generation: {}", generationId);

        recruitmentCommandRepository.deleteByGenerationId(generationId);

        log.info("All recruitments deleted for generation: {}", generationId);
    }
}
