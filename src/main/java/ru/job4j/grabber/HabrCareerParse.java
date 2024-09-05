package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.job4j.grabber.utils.DateTimeParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class HabrCareerParse implements DateTimeParser {

    private static final String SOURCE_LINK = "https://career.habr.com";
    public static final String PREFIX = "/vacancies?page=";
    public static final String SUFFIX = "&q=Java%20developer&type=all";
    private static final int pageCount = 5;

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= pageCount; i++) {
            int pageNumber = i;
            String fullLink = "%s%s%d%s".formatted(SOURCE_LINK, PREFIX, pageNumber, SUFFIX);
            Connection connection = Jsoup.connect(fullLink);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                Element date = row.select(".vacancy-card__date").first();
                String vacancyDate = date.child(0).attr("datetime");
                LocalDateTime formatedDate = new HabrCareerParse().parse(vacancyDate);
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                System.out.printf("%s %s %s%n", vacancyName, formatedDate, link);
            });
        }
    }

    @Override
    public LocalDateTime parse(String parse) {
        return OffsetDateTime.parse(parse, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
    }
}