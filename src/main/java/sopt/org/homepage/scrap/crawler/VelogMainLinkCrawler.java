package sopt.org.homepage.scrap.crawler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import sopt.org.homepage.scrap.LinkCrawler;
import sopt.org.homepage.scrap.LinkSource;
import sopt.org.homepage.scrap.dto.CreateScraperResponseDto;

@Component
public class VelogMainLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	@Override
	public LinkSource supportSource() {
		return LinkSource.VELOG_MAIN;
	}

	@Override
	public CreateScraperResponseDto crawl(String link) throws IOException {
		Document document = scrap(link);
		String title = extractTitle(document);
		String image = extractImage(document);
		String description = extractDescription(document);
		String platform = "벨로그";
		return new CreateScraperResponseDto(image, title, description, link, platform);
	}

	private Document scrap(String link) throws IOException {
		URL url = new URL(link);

		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(TIMEOUT_MILLISECONDS);
		connection.setReadTimeout(TIMEOUT_MILLISECONDS);

		InputStream inputStream = connection.getInputStream();

		Document doc = Jsoup.parse(inputStream, "UTF-8", link);
		inputStream.close();

		return doc;
	}

	private String extractTitle(Document document) {
		Element head = document.head();
		return head.getElementsByTag("title").text();
	}

	private String extractImage(Document document) {
		Element image = document.selectFirst(
			"body > div > div:nth-child(2) > div:nth-child(2) > main > div > div:nth-child(1) > div:nth-child(1) > div > a > img");
		if (image == null) {
			return EMPTY_STRING;
		}
		return image.attr("src");
	}

	private String extractDescription(Document document) {
		Element description = document.selectFirst(
			"body > div > div:nth-child(2) > div:nth-child(2) > main > div > div:nth-child(1) > div:nth-child(1) > div > div > div:nth-child(2)");
		if (description == null) {
			return EMPTY_STRING;
		}
		return description.text();
	}
}
