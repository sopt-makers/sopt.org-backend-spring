package sopt.org.homepage.review.service.command.dto;

import java.util.ArrayList;
import java.util.List;
import sopt.org.homepage.common.type.PartType;
import sopt.org.homepage.review.controller.dto.CreateReviewReq;
import sopt.org.homepage.review.domain.vo.CategoryType;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;

/**
 * 리뷰 생성 커맨드
 * <p>
 * Controller Request와 외부 스크래핑 결과를 결합하여 생성
 */
public record CreateReviewCommand(
        // 스크래핑 결과
        String title,
        String description,
        String thumbnailUrl,
        String platform,

        // 사용자 입력
        String url,
        String category,
        List<String> subjects,
        String authorName,
        String authorProfileImageUrl,
        Integer generation,
        PartType partType
) {
    /**
     * Request와 스크래핑 결과로부터 Command 생성
     * <p>
     * 세부 주제 추출 로직 포함: - "전체 활동": subActivities 사용 - "서류/면접": subRecruiting 사용 - 그 외: 빈 리스트
     */
    public static CreateReviewCommand from(CreateReviewReq request, CreateScraperResponseDto scrapResult) {
        List<String> subjects = extractSubjects(request);

        return new CreateReviewCommand(
                scrapResult.getTitle(),
                scrapResult.getDescription(),
                scrapResult.getThumbnailUrl(),
                scrapResult.getPlatform(),
                request.link(),
                request.mainCategory(),
                subjects,
                request.author(),
                request.authorProfileImageUrl(),
                request.generation(),
                request.part()
        );
    }

    /**
     * 요청으로부터 세부 주제 추출
     * <p>
     * 비즈니스 규칙: CategoryType Enum을 활용하여 타입 안전하게 처리 - "전체 활동" 카테고리: subActivities 목록 사용 - "서류/면접" 카테고리: subRecruiting 단일
     * 값 사용 - 기타 카테고리: 빈 리스트
     */
    private static List<String> extractSubjects(CreateReviewReq request) {
        List<String> subjects = new ArrayList<>();
        CategoryType categoryType = CategoryType.from(request.mainCategory());
        if (categoryType == CategoryType.ACTIVITY) {
            if (request.subActivities() != null) {
                subjects.addAll(request.subActivities());
            }
        } else if (categoryType == CategoryType.RECRUITING) {
            if (request.subRecruiting() != null) {
                subjects.add(request.subRecruiting());
            }
        }
        return subjects;
    }
}
