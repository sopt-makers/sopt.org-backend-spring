package sopt.org.homepage.soptstory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sopt.org.homepage.common.util.IpAddressUtil;
import sopt.org.homepage.exception.BusinessLogicException;
import sopt.org.homepage.soptstory.controller.dto.GetSoptStoryListRequest;
import sopt.org.homepage.soptstory.controller.dto.PaginatedSoptStoryResponse;
import sopt.org.homepage.soptstory.service.query.SoptStoryQueryService;
import sopt.org.homepage.soptstory.service.query.dto.SoptStoryPageView;
import sopt.org.homepage.soptstory.service.query.dto.SoptStorySearchCond;

/**
 * SoptStory Query Controller
 *
 * 책임:
 * - SoptStory 목록 조회 API
 * - Request/Response 변환
 */
@Tag(name = "SoptStory Query", description = "SoptStory 조회 API")
@RestController
@RequestMapping("/soptstory/")
@RequiredArgsConstructor
public class SoptStoryQueryController {

    private final SoptStoryQueryService soptStoryQueryService;

    /**
     * SoptStory 목록 조회
     *
     * 프로세스:
     * 1. IP 주소 추출 및 검증
     * 2. 검색 조건 생성
     * 3. Service 호출
     * 4. Response 변환
     *
     * @param request 조회 요청 (sort, pageNo, limit)
     * @param httpRequest HTTP 요청 (IP 추출용)
     * @return 페이지네이션된 SoptStory 목록
     */
    @GetMapping
    @Operation(summary = "솝트스토리 리스트 조회(정렬)")
    public ResponseEntity<PaginatedSoptStoryResponse> getSoptStoryList(
            @ParameterObject @Valid @ModelAttribute GetSoptStoryListRequest request,
            HttpServletRequest httpRequest
    ) {
        // 1. IP 주소 추출 및 검증
        String ip = extractAndValidateIp(httpRequest);

        // 2. 검색 조건 생성
        SoptStorySearchCond searchCond = new SoptStorySearchCond(
                request.sort(),
                request.pageNo(),
                request.limit()
        );

        // 3. Service 호출
        SoptStoryPageView pageView = soptStoryQueryService.getSoptStoryList(searchCond, ip);

        // 4. Response 변환
        PaginatedSoptStoryResponse response = PaginatedSoptStoryResponse.from(pageView);

        return ResponseEntity.ok(response);
    }

    // ===== Private Helper Methods =====

    /**
     * IP 주소 추출 및 검증
     */
    private String extractAndValidateIp(HttpServletRequest request) {
        String ip = IpAddressUtil.getClientIpAddress(request);

        if (!IpAddressUtil.isValidIpAddress(ip)) {
            throw new BusinessLogicException("Unable to determine client IP address");
        }

        return ip;
    }
}
