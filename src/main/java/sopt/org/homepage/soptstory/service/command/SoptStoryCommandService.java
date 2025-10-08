package sopt.org.homepage.soptstory.service.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.soptstory.domain.SoptStory;
import sopt.org.homepage.soptstory.domain.SoptStoryLike;
import sopt.org.homepage.soptstory.domain.vo.IpAddress;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryContent;
import sopt.org.homepage.soptstory.domain.vo.SoptStoryUrl;
import sopt.org.homepage.soptstory.exception.AlreadyLikedException;
import sopt.org.homepage.soptstory.exception.DuplicateSoptStoryUrlException;
import sopt.org.homepage.soptstory.exception.NotLikedException;
import sopt.org.homepage.soptstory.exception.SoptStoryNotFoundException;
import sopt.org.homepage.soptstory.repository.command.SoptStoryCommandRepository;
import sopt.org.homepage.soptstory.repository.command.SoptStoryLikeCommandRepository;
import sopt.org.homepage.soptstory.service.command.dto.*;

/**
 * SoptStory Command Service
 *
 * 책임:
 * - SoptStory 생성
 * - 좋아요 추가/취소
 * - 트랜잭션 관리
 * - 도메인 로직은 Entity에 위임
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SoptStoryCommandService {

    private final SoptStoryCommandRepository soptStoryCommandRepository;
    private final SoptStoryLikeCommandRepository soptStoryLikeCommandRepository;

    /**
     * SoptStory 생성
     *
     * 비즈니스 규칙:
     * 1. URL 중복 체크
     * 2. Value Object 생성 및 검증
     * 3. 도메인 팩토리 메서드로 생성
     *
     * @param command 생성 정보
     * @return 생성된 SoptStory ID
     * @throws DuplicateSoptStoryUrlException URL이 이미 존재하는 경우
     */
    public SoptStoryId createSoptStory(CreateSoptStoryCommand command) {
        // 1. URL 중복 체크
        SoptStoryUrl url = new SoptStoryUrl(command.articleUrl());
        if (soptStoryCommandRepository.existsByUrl_Value(url.getValue())) {
            throw new DuplicateSoptStoryUrlException(url.getValue());
        }

        // 2. Value Object 생성 (각 VO에서 검증)
        SoptStoryContent content = new SoptStoryContent(
                command.title(),
                command.description(),
                command.thumbnailUrl()
        );

        // 3. 도메인 팩토리 메서드로 생성
        SoptStory soptStory = SoptStory.create(content, url);

        // 4. 저장
        SoptStory saved = soptStoryCommandRepository.save(soptStory);

        return new SoptStoryId(saved.getId());
    }

    /**
     * 좋아요 추가
     *
     * 비즈니스 규칙:
     * 1. SoptStory 존재 확인
     * 2. 중복 좋아요 체크
     * 3. 좋아요 생성 및 SoptStory 좋아요 수 증가
     *
     * @param command 좋아요 정보
     * @return 생성된 좋아요 ID
     * @throws SoptStoryNotFoundException SoptStory가 존재하지 않는 경우
     * @throws AlreadyLikedException 이미 좋아요를 누른 경우
     */
    public SoptStoryLikeId like(LikeSoptStoryCommand command) {
        // 1. SoptStory 조회
        SoptStory soptStory = soptStoryCommandRepository.findById(command.soptStoryId())
                .orElseThrow(() -> new SoptStoryNotFoundException(command.soptStoryId()));

        // 2. 중복 좋아요 체크
        if (soptStoryLikeCommandRepository.existsBySoptStory_IdAndIpAddress_Value(
                command.soptStoryId(),
                command.ip()
        )) {
            throw new AlreadyLikedException(command.soptStoryId(), command.ip());
        }

        // 3. IP Address VO 생성
        IpAddress ipAddress = new IpAddress(command.ip());

        // 4. 좋아요 생성 (도메인 팩토리 메서드)
        SoptStoryLike like = SoptStoryLike.create(soptStory, ipAddress);
        SoptStoryLike saved = soptStoryLikeCommandRepository.save(like);

        // 5. SoptStory 좋아요 수 증가 (도메인 로직)
        soptStory.incrementLike();

        return new SoptStoryLikeId(saved.getId());
    }

    /**
     * 좋아요 취소
     *
     * 비즈니스 규칙:
     * 1. SoptStory 존재 확인
     * 2. 좋아요 존재 확인
     * 3. 좋아요 삭제 및 SoptStory 좋아요 수 감소
     *
     * @param command 좋아요 취소 정보
     * @return 삭제된 좋아요 ID
     * @throws SoptStoryNotFoundException SoptStory가 존재하지 않는 경우
     * @throws NotLikedException 좋아요를 누르지 않은 경우
     */
    public SoptStoryLikeId unlike(UnlikeSoptStoryCommand command) {
        // 1. SoptStory 조회
        SoptStory soptStory = soptStoryCommandRepository.findById(command.soptStoryId())
                .orElseThrow(() -> new SoptStoryNotFoundException(command.soptStoryId()));

        // 2. 좋아요 조회
        SoptStoryLike like = soptStoryLikeCommandRepository
                .findBySoptStory_IdAndIpAddress_Value(command.soptStoryId(), command.ip())
                .orElseThrow(() -> new NotLikedException(command.soptStoryId(), command.ip()));

        // 3. SoptStory 좋아요 수 감소 (도메인 로직)
        soptStory.decrementLike();

        // 4. 좋아요 삭제
        Long likeId = like.getId();
        soptStoryLikeCommandRepository.delete(like);

        return new SoptStoryLikeId(likeId);
    }
}