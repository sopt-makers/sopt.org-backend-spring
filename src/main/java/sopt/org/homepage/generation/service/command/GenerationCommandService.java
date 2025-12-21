package sopt.org.homepage.generation.service.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.generation.domain.Generation;
import sopt.org.homepage.generation.repository.command.GenerationCommandRepository;
import sopt.org.homepage.generation.repository.query.GenerationQueryRepository;
import sopt.org.homepage.generation.service.command.dto.CreateGenerationCommand;
import sopt.org.homepage.generation.service.command.dto.UpdateGenerationCommand;
import sopt.org.homepage.global.exception.ClientBadRequestException;

/**
 * GenerationCommandService
 * <p>
 * 책임: Generation 엔티티의 생성, 수정, 삭제 처리 - Command 작업만 담당 - 트랜잭션 관리 - 비즈니스 검증
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GenerationCommandService {

    private final GenerationCommandRepository generationCommandRepository;
    private final GenerationQueryRepository generationQueryRepository;

    /**
     * 새로운 기수 생성
     *
     * @param command 기수 생성 커맨드
     * @return 생성된 기수 ID
     * @throws ClientBadRequestException 이미 존재하는 기수인 경우
     */
    public Integer createGeneration(CreateGenerationCommand command) {
        log.info("Creating generation: {}", command.id());

        // 중복 검증
        if (generationQueryRepository.existsById(command.id())) {
            throw new ClientBadRequestException(
                    String.format("Generation %d already exists", command.id())
            );
        }

        // 엔티티 생성 및 저장
        Generation generation = command.toEntity();
        Generation saved = generationCommandRepository.save(generation);

        log.info("Generation created successfully: {}", saved.getId());
        return saved.getId();
    }

    /**
     * 기수 정보 전체 수정
     *
     * @param command 기수 수정 커맨드
     * @throws ClientBadRequestException 기수가 존재하지 않는 경우
     */
    public void updateGeneration(UpdateGenerationCommand command) {
        log.info("Updating generation: {}", command.id());

        Generation generation = generationQueryRepository.findById(command.id())
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Generation %d not found", command.id())
                ));

        generation.update(
                command.name(),
                command.headerImage(),
                command.recruitHeaderImage(),
                command.brandingColor().toVO(),
                command.mainButton().toVO()
        );

        log.info("Generation updated successfully: {}", command.id());
    }

    /**
     * 헤더 이미지만 수정
     */
    public void updateHeaderImage(Integer generationId, String headerImageUrl) {
        log.info("Updating header image for generation: {}", generationId);

        Generation generation = generationQueryRepository.findById(generationId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Generation %d not found", generationId)
                ));

        generation.updateHeaderImage(headerImageUrl);

        log.info("Header image updated successfully: {}", generationId);
    }

    /**
     * 모집 헤더 이미지만 수정
     */
    public void updateRecruitHeaderImage(Integer generationId, String recruitHeaderImageUrl) {
        log.info("Updating recruit header image for generation: {}", generationId);

        Generation generation = generationQueryRepository.findById(generationId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Generation %d not found", generationId)
                ));

        generation.updateRecruitHeaderImage(recruitHeaderImageUrl);

        log.info("Recruit header image updated successfully: {}", generationId);
    }

    /**
     * 브랜딩 컬러만 수정
     */
    public void updateBrandingColor(
            Integer generationId,
            UpdateGenerationCommand.BrandingColorCommand brandingColor
    ) {
        log.info("Updating branding color for generation: {}", generationId);

        Generation generation = generationQueryRepository.findById(generationId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Generation %d not found", generationId)
                ));

        generation.updateBrandingColor(brandingColor.toVO());

        log.info("Branding color updated successfully: {}", generationId);
    }

    /**
     * 메인 버튼 설정만 수정
     */
    public void updateMainButton(
            Integer generationId,
            UpdateGenerationCommand.MainButtonCommand mainButton
    ) {
        log.info("Updating main button for generation: {}", generationId);

        Generation generation = generationQueryRepository.findById(generationId)
                .orElseThrow(() -> new ClientBadRequestException(
                        String.format("Generation %d not found", generationId)
                ));

        generation.updateMainButton(mainButton.toVO());

        log.info("Main button updated successfully: {}", generationId);
    }

    /**
     * 기수 삭제
     *
     * @param generationId 삭제할 기수 ID
     * @throws ClientBadRequestException 기수가 존재하지 않는 경우
     */
    public void deleteGeneration(Integer generationId) {
        log.info("Deleting generation: {}", generationId);

        if (!generationQueryRepository.existsById(generationId)) {
            throw new ClientBadRequestException(
                    String.format("Generation %d not found", generationId)
            );
        }

        generationCommandRepository.deleteById(generationId);

        log.info("Generation deleted successfully: {}", generationId);
    }
}
