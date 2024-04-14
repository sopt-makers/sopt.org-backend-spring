package sopt.org.homepage.project.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record Link(
        @Schema(description = "웹사이트, 구글 플레이스토어, 앱스토어, Github, 발표영상 등 프로젝트에 관련된 링크의 종류", requiredMode = Schema.RequiredMode.REQUIRED, example = "website")
        LinkType title,

        @Schema(description = "링크의 url 주소", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://example.com")
        String url
) {
}
