package sopt.org.homepage.sopticle;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.sopticle.dto.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.dto.SopticleResponseDto;

@Tag(name = "Sopticle")
@RestController
@RequestMapping("sopticle")
@RequiredArgsConstructor
public class SopticleController {

    private final SopticleService sopticleService;

    @GetMapping("")
    @Operation(summary = "Sopticle 리스트 조회")
    public ResponseEntity<PaginateResponseDto<SopticleResponseDto>> getSopticleList(
            @ParameterObject @ModelAttribute GetSopticleListRequestDto getSopticleListRequestDto,
            @RequestHeader(value = "session-id", required = false) String session
    ) {
        if (session == null) {
            throw new BusinessLogicException("session-id is required");
        }
        return ResponseEntity.ok(sopticleService.paginateSopticles(getSopticleListRequestDto, session));
    }
}
