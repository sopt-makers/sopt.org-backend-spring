package sopt.org.homepage.soptstory;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
import sopt.org.homepage.common.util.IpAddressUtil;
import sopt.org.homepage.config.AuthConfig;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.soptstory.dto.request.CreateSoptStoryDto;
import sopt.org.homepage.soptstory.dto.request.GetSoptStoryListRequestDto;
import sopt.org.homepage.soptstory.dto.response.CreateSoptStoryResponseDto;
import sopt.org.homepage.soptstory.dto.response.LikeSoptStoryResponseDto;
import sopt.org.homepage.soptstory.dto.response.SoptStoryResponseDto;
import sopt.org.homepage.soptstory.service.SoptStoryService;

@Tag(name = "SoptStory")
@RestController
//@RequestMapping("soptstory")// 외부 요청 확인
@RequiredArgsConstructor
public class SoptStoryController {

    private final SoptStoryService soptStoryService;
    private final AuthConfig authConfig;

    @GetMapping("")
    @Operation(summary = "솝트스토리 리스트 조회(정렬)")
    public ResponseEntity<PaginateResponseDto<SoptStoryResponseDto>> getSoptStoryList(
            @ParameterObject @ModelAttribute GetSoptStoryListRequestDto getSoptStoryListRequestDto,
            HttpServletRequest request
    ) {
        String ip = IpAddressUtil.getClientIpAddress(request);
        if (!IpAddressUtil.isValidIpAddress(ip)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Unable to determine client IP address");
        }
        return ResponseEntity.ok(soptStoryService.paginateSoptStorys(getSoptStoryListRequestDto, ip));
    }

    @Operation(summary = "솝트스토리 좋아요 누르기")
    @PostMapping("/{id}/like")
    public ResponseEntity<LikeSoptStoryResponseDto> likeSoptStory(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String ip = IpAddressUtil.getClientIpAddress(request);
        if (!IpAddressUtil.isValidIpAddress(ip)) {
            throw new BusinessLogicException("Unable to determine client IP address");
        }
        return ResponseEntity.ok(soptStoryService.like(id, ip));
    }

    @Operation(summary = "솝트 스토리 좋아요 취소하기")
    @PostMapping("/{id}/unlike")
    public ResponseEntity<LikeSoptStoryResponseDto> unlikeSoptStory(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        String ip = IpAddressUtil.getClientIpAddress(request);
        if (!IpAddressUtil.isValidIpAddress(ip)) {
            throw new BusinessLogicException("Unable to determine client IP address");
        }
        return ResponseEntity.ok(soptStoryService.unlike(id, ip));
    }

    @PostMapping
    @Operation(summary = "솝트스토리 생성", description = "솝트스토리를 생성합니다.")
    public ResponseEntity<CreateSoptStoryResponseDto> createSoptStory(
            @Valid @RequestBody CreateSoptStoryDto dto,
            @RequestHeader("api-key") String apiKey
    ) {
        if (apiKey == null) {
            throw new BusinessLogicException("api-key is required");
        }

        if (!apiKey.equals(authConfig.getApiKey())) {
            throw new BusinessLogicException("api-key is invalid");
        }

        return ResponseEntity.ok(soptStoryService.createSoptStory(dto));
    }
}
