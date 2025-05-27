package sopt.org.homepage.internal.playground;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.internal.playground.dto.request.ScrapLinkRequestDto;
import sopt.org.homepage.internal.playground.dto.response.ScrapLinkResponseDto;

@RestController
@RequiredArgsConstructor
@RequestMapping("internal")
@Tag(name = "Internal")
public class InternalController {

	private final PlaygroundService playgroundService;
	private final AuthConfig authConfig;

	@PostMapping("scrap")
	@Operation(summary = "블로그 링크 정보 스크랩하기", description = "Playground 에서 솝티클 정보 스크래핑에 필요한 API 입니다.")
	public ResponseEntity<ScrapLinkResponseDto> scrapLink(
		@Valid @RequestBody ScrapLinkRequestDto dto,
		@RequestHeader("api-key") String apiKey
	) {
		if (apiKey == null) {
			throw new BusinessLogicException("api-key is required");
		}

		if (!apiKey.equals(authConfig.getApiKey())) {
			throw new BusinessLogicException("api-key is invalid");
		}

		return ResponseEntity.ok(playgroundService.scrapLink(dto));
	}

}


