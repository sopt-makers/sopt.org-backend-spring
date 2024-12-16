package sopt.org.homepage.sopticle;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.sopticle.dto.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.repository.SopticleLikeRepository;
import sopt.org.homepage.sopticle.dto.SopticleResponseDto;
import sopt.org.homepage.sopticle.entity.SopticleEntity;
import sopt.org.homepage.sopticle.repository.SopticleQueryRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SopticleService {

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
                .uploadedAt(entity.getCreatedAt().toLocalDateTime())
                .likeCount(entity.getLikeCount())
                .liked(liked)
                .build();
    }
}