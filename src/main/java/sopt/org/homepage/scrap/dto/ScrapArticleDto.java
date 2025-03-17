package sopt.org.homepage.scrap.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ScrapArticleDto {

	@Schema(description = "아티클 주소", required = true)
	@NotEmpty(message = "URL은 필수입니다")
	private String articleUrl;
}
