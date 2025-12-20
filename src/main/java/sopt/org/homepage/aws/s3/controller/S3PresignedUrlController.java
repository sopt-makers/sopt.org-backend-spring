package sopt.org.homepage.aws.s3.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sopt.org.homepage.aws.s3.S3Service;
import sopt.org.homepage.aws.s3.dto.PresignedUrlRequest;
import sopt.org.homepage.aws.s3.dto.PresignedUrlResponse;

@Slf4j
@RestController
@RequestMapping("s3")
@RequiredArgsConstructor
@Tag(name = "S3 Presigned URL", description = "S3 파일 업로드를 위한 Presigned URL 발급 API (Lambda 환경용)")
public class S3PresignedUrlController {

    private final S3Service s3Service;

    @PostMapping("/presigned-url")
    @Operation(
            summary = "S3 Presigned URL 발급",
            description = """
                    클라이언트가 직접 S3에 파일을 업로드할 수 있는 서명된 URL을 발급합니다.
                    
                    **사용 흐름:**
                    1. 이 API 호출 → presignedUrl, fileUrl 수신
                    2. presignedUrl로 PUT 요청 (파일 바이너리 전송)
                    3. fileUrl을 뉴스/프로젝트 생성 API에 전달
                    
                    **허용 파일 형식:** jpeg, jpg, png, gif, webp
                    
                    **URL 유효 시간:** 10분
                    
                    **기존 API와의 차이점:**
                    - 기존: generatePresignedUrl(fileName, path) - 서버가 Content-Type 추측
                    - 신규: 클라이언트가 Content-Type 직접 전달 (Lambda 환경 대응)
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Presigned URL 발급 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PresignedUrlResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (허용되지 않는 파일 형식 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                                "status": 400,
                                                "message": "허용되지 않는 파일 형식입니다: application/pdf. 허용된 형식: [image/jpeg, image/png, image/gif, image/webp]"
                                            }
                                            """
                            )
                    )
            )
    })
    public ResponseEntity<PresignedUrlResponse> getPresignedUrl(
            @Valid @RequestBody PresignedUrlRequest request
    ) {
        log.info("Presigned URL 발급 요청 (V2) - fileName: {}, contentType: {}, directory: {}",
                request.getFileName(), request.getContentType(), request.getDirectory());

        PresignedUrlResponse response = s3Service.generatePresignedUrlV2(request);

        log.info("Presigned URL 발급 완료 (V2) - fileKey: {}", response.getFileKey());

        return ResponseEntity.ok(response);
    }
}
