package sopt.org.homepage.scrap.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateScraperResponseDto {
	private String thumbnailUrl;
	private String title;
	private String description;
	private String articleUrl;
	private String platform;
}
