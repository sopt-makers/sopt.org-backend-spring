package sopt.org.homepage.soptstory.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.scrap.service.ScraperService;
import sopt.org.homepage.soptstory.dto.request.CreateSopticleDto;
import sopt.org.homepage.soptstory.dto.request.GetSopticleListRequestDto;
import sopt.org.homepage.soptstory.dto.response.CreateSopticleResponseDto;
import sopt.org.homepage.soptstory.dto.response.LikeSopticleResponseDto;
import sopt.org.homepage.soptstory.dto.response.SopticleResponseDto;
import sopt.org.homepage.soptstory.entity.SoptStoryEntity;
import sopt.org.homepage.soptstory.entity.SoptStoryLikeEntity;
import sopt.org.homepage.soptstory.repository.SopticleLikeRepository;
import sopt.org.homepage.soptstory.repository.SopticleQueryRepository;
import sopt.org.homepage.soptstory.repository.SopticleRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SopticleServiceImpl implements SopticleService {
	private final SopticleRepository sopticleRepository;
	private final SopticleQueryRepository sopticleQueryRepository;
	private final SopticleLikeRepository sopticleLikeRepository;
	private final ScraperService scraperService;

	@Override
	public PaginateResponseDto<SopticleResponseDto> paginateSopticles(GetSopticleListRequestDto requestDto,
		String sessionId) {
		var sopticles = sopticleQueryRepository.findAllSorted(requestDto);
		var totalCount = (int)sopticleRepository.count();

		var likedSopticles = sopticleLikeRepository.findAllBySessionIdAndSopticleIn(sessionId, sopticles);

		var sopticleDtos = sopticles.stream()
			.map(sopticle -> toSopticleResponseDto(
				sopticle,
				likedSopticles.stream()
					.anyMatch(like -> like.getSoptStroy().getId().equals(sopticle.getId()))
			))
			.toList();

		return new PaginateResponseDto<>(
			sopticleDtos,
				totalCount,
			requestDto.getLimit(),
			requestDto.getPageNo()
		);
	}

	@Override
	@Transactional
	public LikeSopticleResponseDto like(Long id, String session) {
		SoptStoryEntity sopticle = sopticleRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NotFoundSopticle id: " + id));

		if (isLiked(id, session)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "AlreadyLike");
		}

		SoptStoryLikeEntity sopticleLike = SoptStoryLikeEntity.builder()
			.soptStroy(sopticle)
			.sessionId(session)
			.build();

		sopticleLikeRepository.save(sopticleLike);
		sopticle.incrementLikeCount();

		return toLikeSopticleResponseDto(sopticleLike);
	}
	private boolean isLiked(Long sopticleId, String session) {
		return sopticleLikeRepository.existsBySopticleIdAndSessionId(sopticleId, session);
	}

	@Override
	@Transactional
	public LikeSopticleResponseDto unlike(Long id, String session) {
		SoptStoryEntity sopticle = sopticleRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "NotFoundSopticle id: " + id));

		SoptStoryLikeEntity sopticleLike = sopticleLikeRepository.findBySopticleIdAndSessionId(id, session)
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

		SoptStoryEntity sopticle = SoptStoryEntity.builder()
				.thumbnailUrl(scrapResult.getThumbnailUrl())
				.title(scrapResult.getTitle())
				.description(scrapResult.getDescription())
				.soptStoryUrl(scrapResult.getArticleUrl())
				.build();

		sopticleRepository.save(sopticle);

		return CreateSopticleResponseDto.builder()
			.thumbnailUrl(scrapResult.getThumbnailUrl())
			.title(scrapResult.getTitle())
			.description(scrapResult.getDescription())
			.sopticleUrl(scrapResult.getArticleUrl())
			.build();

	}

	private SopticleResponseDto toSopticleResponseDto(SoptStoryEntity entity, boolean liked) {
		return SopticleResponseDto.builder()
			.id(entity.getId())
			.thumbnailUrl(entity.getThumbnailUrl())
			.title(entity.getTitle())
			.description(entity.getDescription())
			.url(entity.getSoptStoryUrl())
			.uploadedAt(entity.getCreatedAt())
			.likeCount(entity.getLikeCount())
			.build();
	}

	private LikeSopticleResponseDto toLikeSopticleResponseDto(SoptStoryLikeEntity entity) {
		return LikeSopticleResponseDto.builder()
			.id(entity.getId())
			.sopticleId(entity.getSoptStroy().getId())
			.sessionId(entity.getSessionId())
			.build();
	}
}
