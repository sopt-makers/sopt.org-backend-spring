package sopt.org.homepage.sopticle.scrap;

import java.io.IOException;

import sopt.org.homepage.sopticle.scrap.dto.CreateScraperResponseDto;

public interface LinkCrawler {

	LinkSource supportSource();

	CreateScraperResponseDto crawl(String link) throws IOException;
}
