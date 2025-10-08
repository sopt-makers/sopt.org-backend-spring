package sopt.org.homepage.soptstory.service.command.dto;

/**
 * SoptStory 생성 Command
 *
 * 책임:
 * - 외부 스크래핑 결과를 도메인 생성에 필요한 형태로 전달
 * - 불변 객체로 안전한 데이터 전달
 *
 * @param title 제목
 * @param description 설명
 * @param thumbnailUrl 썸네일 URL (nullable)
 * @param articleUrl 원문 URL
 */
public record CreateSoptStoryCommand(
        String title,
        String description,
        String thumbnailUrl,
        String articleUrl
) {
    /**
     * 입력값 검증
     */
    public CreateSoptStoryCommand {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("설명은 필수입니다.");
        }
        if (articleUrl == null || articleUrl.isBlank()) {
            throw new IllegalArgumentException("URL은 필수입니다.");
        }
    }
}