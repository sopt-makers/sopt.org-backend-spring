package sopt.org.homepage.sopticle.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.common.type.Part;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;
import sopt.org.homepage.scrap.dto.ScrapArticleDto;
import sopt.org.homepage.scrap.service.ScraperService;
import sopt.org.homepage.sopticle.dto.request.CreateSopticleDto;
import sopt.org.homepage.sopticle.dto.request.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.dto.response.CreateSopticleResponseDto;
import sopt.org.homepage.sopticle.dto.response.LikeSopticleResponseDto;
import sopt.org.homepage.sopticle.dto.response.SopticleResponseDto;
import sopt.org.homepage.sopticle.entity.SopticleEntity;
import sopt.org.homepage.sopticle.entity.SopticleLikeEntity;
import sopt.org.homepage.sopticle.repository.SopticleLikeRepository;
import sopt.org.homepage.sopticle.repository.SopticleQueryRepository;
import sopt.org.homepage.sopticle.repository.SopticleRepository;

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
					.anyMatch(like -> like.getSopticle().getId().equals(sopticle.getId()))
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
	private boolean isLiked(Long sopticleId, String session) {
		return sopticleLikeRepository.existsBySopticleIdAndSessionId(sopticleId, session);
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
				.thumbnailUrl(scrapResult.getThumbnailUrl())
				.title(scrapResult.getTitle())
				.description(scrapResult.getDescription())
				.sopticleUrl(scrapResult.getArticleUrl())
				.build();

		sopticleRepository.save(sopticle);

		return CreateSopticleResponseDto.builder()
			.thumbnailUrl(scrapResult.getThumbnailUrl())
			.title(scrapResult.getTitle())
			.description(scrapResult.getDescription())
			.sopticleUrl(scrapResult.getArticleUrl())
			.build();

	}

	private SopticleResponseDto toSopticleResponseDto(SopticleEntity entity, boolean liked) {
		return SopticleResponseDto.builder()
			.id(entity.getId())
			.thumbnailUrl(entity.getThumbnailUrl())
			.title(entity.getTitle())
			.description(entity.getDescription())
			.url(entity.getSopticleUrl())
			.uploadedAt(entity.getCreatedAt())
			.likeCount(entity.getLikeCount())
			.build();
	}

	private LikeSopticleResponseDto toLikeSopticleResponseDto(SopticleLikeEntity entity) {
		return LikeSopticleResponseDto.builder()
			.id(entity.getId())
			.sopticleId(entity.getSopticle().getId())
			.sessionId(entity.getSessionId())
			.build();
	}
}
