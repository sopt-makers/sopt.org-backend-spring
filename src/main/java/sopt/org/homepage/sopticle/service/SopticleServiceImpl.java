package sopt.org.homepage.sopticle.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.sopticle.dto.request.CreateSopticleAuthorRole;
import sopt.org.homepage.sopticle.dto.request.CreateSopticleDto;
import sopt.org.homepage.sopticle.dto.request.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.dto.response.CreateSopticleResponseDto;
import sopt.org.homepage.sopticle.dto.response.LikeSopticleResponseDto;
import sopt.org.homepage.sopticle.dto.response.SopticleResponseDto;
import sopt.org.homepage.sopticle.entity.SopticleAuthorEntity;
import sopt.org.homepage.sopticle.entity.SopticleEntity;
import sopt.org.homepage.sopticle.entity.SopticleLikeEntity;
import sopt.org.homepage.sopticle.repository.SopticleAuthorRepository;
import sopt.org.homepage.sopticle.repository.SopticleLikeRepository;
import sopt.org.homepage.sopticle.repository.SopticleQueryRepository;
import sopt.org.homepage.sopticle.repository.SopticleRepository;
import sopt.org.homepage.sopticle.scrap.service.ScraperService;
import sopt.org.homepage.sopticle.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.sopticle.scrap.dto.ScrapArticleDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SopticleServiceImpl implements SopticleService {
    private final SopticleRepository sopticleRepository;
    private final SopticleQueryRepository sopticleQueryRepository;
    private final SopticleLikeRepository sopticleLikeRepository;
    private final SopticleAuthorRepository sopticleAuthorRepository;
    private final ScraperService scraperService;

    @Override
    public PaginateResponseDto<SopticleResponseDto> paginateSopticles(GetSopticleListRequestDto requestDto, String sessionId) {
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

    @Override
    @Transactional
    public LikeSopticleResponseDto like(Long id, String session) {
        SopticleEntity sopticle = sopticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NotFoundSopticle id: " + id));

        if (isLiked(id, session)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AlreadyLike");
        }

        SopticleLikeEntity sopticleLike = SopticleLikeEntity.builder()
                .sopticle(sopticle)
                .sessionId(session)
                .build();

        sopticleLikeRepository.save(sopticleLike);
        sopticle.incrementLikeCount();

        return toLikeSopticleResponseDto(sopticleLike);
    }

    @Override
    @Transactional
    public LikeSopticleResponseDto unlike(Long id, String session) {
        SopticleEntity sopticle = sopticleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NotFoundSopticle id: " + id));

        SopticleLikeEntity sopticleLike = sopticleLikeRepository.findBySopticleIdAndSessionId(id, session)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Like 하지 않은 상태입니다."));

        sopticle.decrementLikeCount();
        sopticleLikeRepository.delete(sopticleLike);

        return toLikeSopticleResponseDto(sopticleLike);
    }

    @Override
    @Transactional
    public CreateSopticleResponseDto createSopticle(CreateSopticleDto dto) {
        if (sopticleRepository.existsBySopticleUrl(dto.getLink())) {
            throw new BusinessLogicException("이미 등록된 솝티클입니다.");
        }

        CreateScraperResponseDto scrapResult = scraperService.scrap(new ScrapArticleDto(dto.getLink()));

        SopticleEntity sopticle = SopticleEntity.builder()
                .pgSopticleId(dto.getId())
                .part(convertToPart(dto.getAuthors().get(0).getPart()))
                .generation(dto.getAuthors().get(0).getGeneration())
                .thumbnailUrl(scrapResult.getThumbnailUrl())
                .title(scrapResult.getTitle())
                .description(scrapResult.getDescription())
                .authorId(dto.getAuthors().get(0).getId())
                .authorName(dto.getAuthors().get(0).getName())
                .authorProfileImageUrl(dto.getAuthors().get(0).getProfileImage())
                .sopticleUrl(scrapResult.getArticleUrl())
                .build();

        SopticleEntity savedSopticle = sopticleRepository.save(sopticle);

        List<SopticleAuthorEntity> authorEntities = dto.getAuthors().stream()
                .map(author -> SopticleAuthorEntity.builder()
                        .sopticle(savedSopticle)
                        .pgUserId(author.getId())
                        .name(author.getName())
                        .profileImage(author.getProfileImage())
                        .generation(author.getGeneration())
                        .part(author.getPart().getValue())
                        .build())
                .collect(Collectors.toList());

        sopticleAuthorRepository.saveAll(authorEntities);

        return CreateSopticleResponseDto.builder()
                .id(savedSopticle.getId())
                .part(savedSopticle.getPart())
                .generation(savedSopticle.getGeneration())
                .thumbnailUrl(savedSopticle.getThumbnailUrl())
                .title(savedSopticle.getTitle())
                .description(savedSopticle.getDescription())
                .author(savedSopticle.getAuthorName())
                .authorProfileImageUrl(savedSopticle.getAuthorProfileImageUrl())
                .sopticleUrl(savedSopticle.getSopticleUrl())
                .uploadedAt(savedSopticle.getCreatedAt())
                .build();
    }

    private SopticleResponseDto toSopticleResponseDto(SopticleEntity entity, boolean liked) {
        return SopticleResponseDto.builder()
                .id(entity.getId())
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

    private Part convertToPart(CreateSopticleAuthorRole role) {
        return switch (role) {
            case WEB, WEB_LEADER -> Part.WEB;
            case PLAN, PLAN_LEADER, PRESIDENT, VICE_PRESIDENT,
                    OPERATION_LEADER, MEDIA_LEADER -> Part.PLAN;
            case DESIGN, DESIGN_LEADER -> Part.DESIGN;
            case IOS, IOS_LEADER -> Part.iOS;
            case SERVER, SERVER_LEADER -> Part.SERVER;
            case ANDROID, ANDROID_LEADER -> Part.ANDROID;
            default -> throw new IllegalArgumentException("Unknown role: " + role);
        };
    }
}
