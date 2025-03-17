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
public class BrunchLinkCrawler implements LinkCrawler {

	private static final int TIMEOUT_MILLISECONDS = 10000;
	private static final String EMPTY_STRING = "";

	@Override
	public LinkSource supportSource() {
		return LinkSource.BRUNCH;
	}

	@Override
	public CreateScraperResponseDto crawl(String link) throws IOException {
		Document target = scrap(link);
		String title = fetchMeta(target, "title");
		String url = fetchMeta(target, "url");
		String image = "https:" + fetchMeta(target, "image");
		String description = fetchMeta(target, "description");
		String platform = "브런치";
		return new CreateScraperResponseDto(image, title, description, url, platform);
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

	private String fetchMeta(Document target, String tag) {
		Element element = target.selectFirst("meta[property=og:%s]".formatted(tag));
		if (element == null) {
			return EMPTY_STRING;
		}
		return element.attr("content");
	}
}
