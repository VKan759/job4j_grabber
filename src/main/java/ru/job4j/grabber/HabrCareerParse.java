package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HabrCareerParse implements Parse {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int PAGE_COUNT = 1;
    private final DateTimeParser dateTimeParser;

    public HabrCareerParse(DateTimeParser dateTimeParser) {
        this.dateTimeParser = dateTimeParser;
    }

    public static void main(String[] args) throws IOException {
        DateTimeParser parser = new DateTimeParseUser();
        HabrCareerParse habrCareerParse = new HabrCareerParse(parser);
        List<Post> list = habrCareerParse.list("https://career.habr.com");
        System.out.println(list);
    }

    private String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(link);
        Document document = connection.get();
        Elements rows = document.select(".vacancy-description__text");
        StringBuilder description = new StringBuilder();
        for (Element row : rows) {
            String text = row.text();
            if (!text.isEmpty()) {
                description.append(text);
            }
        }
        return description.toString().trim();
    }


    @Override
    public List<Post> list(String link) throws IOException {
        List<Post> result = new ArrayList<>();
        for (int i = 1; i <= PAGE_COUNT; i++) {
            int pageNumber = i;
            String fullLink = "%s%s%d%s".formatted(link, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Post post = new Post();
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                Element date = row.select(".vacancy-card__date").first();
                String vacancyDate = date.child(0).attr("datetime");
                String vacancyLink = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                post.setTitle(vacancyName);
                post.setLink(vacancyLink);
                post.setCreated(dateTimeParser.parse(vacancyDate));
                try {
                    String description = retrieveDescription(vacancyLink);
                    post.setDescription(description);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                result.add(post);
            });
        }
        return result;
    }
}