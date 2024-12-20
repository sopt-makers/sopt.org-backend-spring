package sopt.org.homepage.sopticle;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import sopt.org.homepage.common.dto.PaginateResponseDto;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.sopticle.dto.GetSopticleListRequestDto;
import sopt.org.homepage.sopticle.dto.LikeSopticleResponseDto;
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
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "session-id is required");
        }
        return ResponseEntity.ok(sopticleService.paginateSopticles(getSopticleListRequestDto, session));
    }



    @Operation(summary = "Sopticle 좋아요 누르기")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "이미 좋아요를 누른 게시글입니다. | session-id is required"),
            @ApiResponse(responseCode = "404", description = "해당 sopticle이 존재하지 않습니다.")
    })
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공"),
            @ApiResponse(responseCode = "400", description = "좋아요를 누르지 않았습니다. | session-id is required"),
            @ApiResponse(responseCode = "404", description = "해당 sopticle이 존재하지 않습니다.")
    })
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



}