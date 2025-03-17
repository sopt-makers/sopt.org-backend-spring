package sopt.org.homepage.scrap;

import java.io.IOException;

import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;

public interface LinkCrawler {

	LinkSource supportSource();

	CreateScraperResponseDto crawl(String link) throws IOException;
}
