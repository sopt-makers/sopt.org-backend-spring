package sopt.org.homepage.sopticle;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.sopticle.dto.request.CreateSopticleDto;
import sopt.org.homepage.sopticle.dto.request.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.dto.response.CreateSopticleResponseDto;
import sopt.org.homepage.sopticle.dto.response.LikeSopticleResponseDto;
import sopt.org.homepage.sopticle.dto.response.SopticleResponseDto;
import sopt.org.homepage.sopticle.service.SopticleService;

@Tag(name = "Sopticle")
@RestController
@RequestMapping("sopticle")
@RequiredArgsConstructor
public class SopticleController {

    private final SopticleService sopticleService;
    private final AuthConfig authConfig;

    @GetMapping("")
    @Operation(summary = "Sopticle 리스트 조회(정렬)")
    public ResponseEntity<PaginateResponseDto<SopticleResponseDto>> getSopticleList(
            @ParameterObject @ModelAttribute GetSopticleListRequestDto getSopticleListRequestDto,
            @RequestHeader(value = "session-id", required = false) String session
    ) {
        if (session == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "session-id is required");
        }
        return ResponseEntity.ok(sopticleService.paginateSopticles(getSopticleListRequestDto, session));
    }

    @Operation(summary = "Sopticle 좋아요 누르기")
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeSopticleResponseDto> likeSopticle(
            @PathVariable Long id,
            @RequestHeader(value = "session-id", required = false) String session
    ) {
        if (session == null) {
            throw new BusinessLogicException("session-id is required");
        }
        return ResponseEntity.ok(sopticleService.like(id, session));
    }

    @Operation(summary = "Sopticle 좋아요 취소하기")
    @PostMapping("/{id}/unlike")
    public ResponseEntity<LikeSopticleResponseDto> unlikeSopticle(
            @PathVariable Long id,
            @RequestHeader(value = "session-id", required = false) String session
    ) {
        if (session == null) {
            throw new BusinessLogicException("session-id is required");
        }
        return ResponseEntity.ok(sopticleService.unlike(id, session));
    }

    @PostMapping
    @Operation(summary = "솝티클 생성", description = "솝티클을 생성합니다.")
    public ResponseEntity<CreateSopticleResponseDto> createSopticle(
            @Valid @RequestBody CreateSopticleDto dto,
            @RequestHeader("api-key") String apiKey
    ) {
        if (apiKey == null) {
            throw new BusinessLogicException("api-key is required");
        }

        if (!apiKey.equals(authConfig.getApiKey())) {
            throw new BusinessLogicException("api-key is invalid");
        }

        return ResponseEntity.ok(sopticleService.createSopticle(dto));
    }
}