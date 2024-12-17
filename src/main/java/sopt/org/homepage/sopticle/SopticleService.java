package sopt.org.homepage.sopticle;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.exception.NotFoundException;
import sopt.org.homepage.sopticle.dto.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.dto.LikeSopticleResponseDto;
import sopt.org.homepage.sopticle.entity.SopticleLikeEntity;
import sopt.org.homepage.sopticle.repository.SopticleLikeRepository;
import sopt.org.homepage.sopticle.dto.SopticleResponseDto;
import sopt.org.homepage.sopticle.entity.SopticleEntity;
import sopt.org.homepage.sopticle.repository.SopticleQueryRepository;
import sopt.org.homepage.sopticle.repository.SopticleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SopticleService {
    private final SopticleRepository sopticleRepository;
    private final SopticleQueryRepository sopticleQueryRepository;
    private final SopticleLikeRepository sopticleLikeRepository;

    public PaginateResponseDto<SopticleResponseDto> paginateSopticles(
            GetSopticleListRequestDto requestDto,
            String sessionId
    ) {
        var sopticles = sopticleQueryRepository.findAllWithFilters(requestDto, sessionId);
        var totalCount = sopticleQueryRepository.countWithFilters(requestDto);

        var likedSopticles = sopticleLikeRepository.findAllBySessionIdAndSopticleIn(sessionId, sopticles);

        var sopticleDtos = sopticles.stream()
                .map(sopticle -> toSopticleResponseDto(
                        sopticle,
                        likedSopticles.stream()
                                .anyMatch(like -> like.getSopticle().getId().equals(sopticle.getId()))
                ))
                .toList();

        return new PaginateResponseDto<>(
                sopticleDtos,
                totalCount.intValue(),
                requestDto.getLimit(),
                requestDto.getPageNo()
        );
    }

    private SopticleResponseDto toSopticleResponseDto(SopticleEntity entity, boolean liked) {
        return SopticleResponseDto.builder()
                .id( entity.getId())
                .part(entity.getPart())
                .generation(entity.getGeneration())
                .thumbnailUrl(entity.getThumbnailUrl())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .author(entity.getAuthorName())
                .authorProfileImageUrl(entity.getAuthorProfileImageUrl())
                .url(entity.getSopticleUrl())
                .uploadedAt(entity.getCreatedAt())
                .likeCount(entity.getLikeCount())
                .liked(liked)
                .build();
    }


    @Transactional
    public LikeSopticleResponseDto like(Long id, String session) {
        SopticleEntity sopticle = sopticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("NotFoundSopticle id: " + id));

        if (isLiked(id, session)) {
            throw new BusinessLogicException("AlreadyLike");
        }

        SopticleLikeEntity sopticleLike = SopticleLikeEntity.builder()
                .sopticle(sopticle)
                .sessionId(session)
                .build();

        sopticleLikeRepository.save(sopticleLike);
        sopticle.incrementLikeCount();

        return toLikeSopticleResponseDto(sopticleLike);
    }

    @Transactional
    public LikeSopticleResponseDto unlike(Long id, String session) {
        SopticleEntity sopticle = sopticleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("NotFoundSopticle id: " + id));

        SopticleLikeEntity sopticleLike = sopticleLikeRepository.findBySopticleIdAndSessionId(id, session)
                .orElseThrow(() -> new BusinessLogicException("Like 하지 않은 상태입니다."));

        sopticle.decrementLikeCount();
        sopticleLikeRepository.delete(sopticleLike);

        return toLikeSopticleResponseDto(sopticleLike);
    }

    private boolean isLiked(Long sopticleId, String session) {
        return sopticleLikeRepository.existsBySopticleIdAndSessionId(sopticleId, session);
    }

    private LikeSopticleResponseDto toLikeSopticleResponseDto(SopticleLikeEntity entity) {
        return LikeSopticleResponseDto.builder()
                .id(entity.getId())
                .sopticleId(entity.getSopticle().getId())
                .sessionId(entity.getSessionId())
                .createdAt(entity.getCreatedAt())
                .build();
    }





}