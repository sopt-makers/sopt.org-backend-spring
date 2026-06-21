package sopt.org.homepage.application.homepage.review.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.application.admin.dto.request.main.review.AddAdminReviewRequestDto;
import sopt.org.homepage.application.admin.dto.request.main.review.EditAdminReviewRequestDto;
import sopt.org.homepage.application.admin.dto.response.main.review.AddAdminReviewResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.review.EditAdminReviewResponseRecordDto;
import sopt.org.homepage.application.admin.dto.response.main.review.GetAdminReviewResponseRecordDto;
import sopt.org.homepage.application.homepage.review.service.HomepageReviewService;
import sopt.org.homepage.global.common.constants.SecurityConstants;

@RestController
@RequiredArgsConstructor
@RequestMapping("homepage-reviews")
@Tag(name = "Admin - HomepageReview", description = "홈페이지 리뷰 관리 API")
public class HomepageReviewController {

    private final HomepageReviewService homepageReviewService;

    @Operation(summary = "홈페이지 리뷰 추가", description = "제목 최대 10자, 내용 최대 200자")
    @SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
    @PostMapping
    public ResponseEntity<AddAdminReviewResponseRecordDto> createReview(
            @RequestBody @Valid AddAdminReviewRequestDto request
    ) {
        AddAdminReviewResponseRecordDto result = homepageReviewService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(summary = "홈페이지 리뷰 목록 조회", description = "ID 오름차순으로 전체 목록을 반환합니다")
    @GetMapping
    public ResponseEntity<List<GetAdminReviewResponseRecordDto>> getReviews() {
        List<GetAdminReviewResponseRecordDto> result = homepageReviewService.getReviews();
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Operation(summary = "홈페이지 리뷰 수정", description = "제목 최대 10자, 내용 최대 200자")
    @SecurityRequirement(name = SecurityConstants.SCHEME_NAME)
    @PatchMapping("/{id}")
    public ResponseEntity<EditAdminReviewResponseRecordDto> editReview(
            @PathVariable Long id,
            @RequestBody @Valid EditAdminReviewRequestDto request
    ) {
        EditAdminReviewResponseRecordDto result = homepageReviewService.edit(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
