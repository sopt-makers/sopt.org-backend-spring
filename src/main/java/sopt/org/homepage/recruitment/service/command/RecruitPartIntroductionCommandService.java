package sopt.org.homepage.recruitment.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.exception.ClientBadRequestException;
import sopt.org.homepage.recruitment.domain.RecruitPartIntroduction;
import sopt.org.homepage.recruitment.domain.vo.PartIntroduction;
import sopt.org.homepage.recruitment.repository.command.RecruitPartIntroductionCommandRepository;
import sopt.org.homepage.recruitment.repository.query.RecruitPartIntroductionQueryRepository;
import sopt.org.homepage.recruitment.service.command.dto.BulkCreateRecruitPartIntroductionsCommand;
import sopt.org.homepage.recruitment.service.command.dto.CreateRecruitPartIntroductionCommand;
import sopt.org.homepage.recruitment.service.command.dto.UpdateRecruitPartIntroductionCommand;

import java.util.List;

/**
 * RecruitPartIntroductionCommandService
 *
 * 책임: RecruitPartIntroduction 엔티티의 생성, 수정, 삭제 처리
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecruitPartIntroductionCommandService {

    private final RecruitPartIntroductionCommandRepository recruitPartIntroductionCommandRepository;
    private final RecruitPartIntroductionQueryRepository recruitPartIntroductionQueryRepository;

    /**
     * 파트 소개 생성
     */
    public Long createRecruitPartIntroduction(CreateRecruitPartIntroductionCommand command) {
        log.info("Creating recruit part introduction {} for generation: {}",
                command.part(), command.generationId());

        // 중복 검증
        if (recruitPartIntroductionQueryRepository.findByGenerationIdAndPart(
                command.generationId(), command.part()).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("RecruitPartIntroduction for %s already exists for generation %d",
                            command.part(), command.generationId())
            );
        }

        RecruitPartIntroduction recruitPartIntroduction = command.toEntity();
        RecruitPartIntroduction saved = recruitPartIntroductionCommandRepository.save(recruitPartIntroduction);

        log.info("RecruitPartIntroduction created: {}", saved.getId());
        return saved.getId();
    }

    /**
     * 파트 소개 일괄 생성 (기존 데이터 모두 삭제 후 재생성)
     */
    public List<Long> bulkCreateRecruitPartIntroductions(BulkCreateRecruitPartIntroductionsCommand command) {
        log.info("Bulk creating recruit part introductions for generation: {}", command.generationId());

        // 1. 기존 파트 소개 모두 삭제
        recruitPartIntroductionCommandRepository.deleteByGenerationId(command.generationId());

        // 2. 새로운 파트 소개 생성
        List<RecruitPartIntroduction> recruitPartIntroductions = command.partIntroductions().stream()
                .map(data -> RecruitPartIntroduction.builder()
                        .generationId(command.generationId())
                        .part(PartType.fromString(data.part()))
                        .introduction(PartIntroduction.builder()
                                .content(data.introduction().content())
                                .preference(data.introduction().preference())
                                .build())
                        .build())
                .toList();

        List<RecruitPartIntroduction> saved =
                recruitPartIntroductionCommandRepository.saveAll(recruitPartIntroductions);

        log.info("Bulk created {} recruit part introductions for generation: {}",
                saved.size(), command.generationId());

        return saved.stream().map(RecruitPartIntroduction::getId).toList();
    }

    /**
     * 파트 소개 수정
     */
    public void updateRecruitPartIntroduction(UpdateRecruitPartIntroductionCommand command) {
        log.info("Updating recruit part introduction: {}", command.id());

        RecruitPartIntroduction recruitPartIntroduction =
                recruitPartIntroductionQueryRepository.findById(command.id())
                        .orElseThrow(() -> new ClientBadRequestException(
                                String.format("RecruitPartIntroduction %d not found", command.id())
                        ));

        recruitPartIntroduction.updateIntroduction(command.introduction().toVO());

        log.info("RecruitPartIntroduction updated: {}", command.id());
    }

    /**
     * 소개 내용만 수정
     */
    public void updateContent(Long id, String content) {
        log.info("Updating recruit part introduction content: {}", id);

        RecruitPartIntroduction recruitPartIntroduction =
                recruitPartIntroductionQueryRepository.findById(id)
                        .orElseThrow(() -> new ClientBadRequestException(
                                String.format("RecruitPartIntroduction %d not found", id)
                        ));

        recruitPartIntroduction.updateContent(content);

        log.info("RecruitPartIntroduction content updated: {}", id);
    }

    /**
     * 선호사항만 수정
     */
    public void updatePreference(Long id, String preference) {
        log.info("Updating recruit part introduction preference: {}", id);

        RecruitPartIntroduction recruitPartIntroduction =
                recruitPartIntroductionQueryRepository.findById(id)
                        .orElseThrow(() -> new ClientBadRequestException(
                                String.format("RecruitPartIntroduction %d not found", id)
                        ));

        recruitPartIntroduction.updatePreference(preference);

        log.info("RecruitPartIntroduction preference updated: {}", id);
    }

    /**
     * 파트 소개 삭제
     */
    public void deleteRecruitPartIntroduction(Long id) {
        log.info("Deleting recruit part introduction: {}", id);

        if (!recruitPartIntroductionQueryRepository.findById(id).isPresent()) {
            throw new ClientBadRequestException(
                    String.format("RecruitPartIntroduction %d not found", id)
            );
        }

        recruitPartIntroductionCommandRepository.deleteById(id);

        log.info("RecruitPartIntroduction deleted: {}", id);
    }

    /**
     * 특정 기수의 모든 파트 소개 삭제
     */
    public void deleteAllByGeneration(Integer generationId) {
        log.info("Deleting all recruit part introductions for generation: {}", generationId);

        recruitPartIntroductionCommandRepository.deleteByGenerationId(generationId);

        log.info("All recruit part introductions deleted for generation: {}", generationId);
    }
}