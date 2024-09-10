package ru.job4j.grabber;

import ru.job4j.grabber.utils.DateTimeParser;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeParseUser implements DateTimeParser {
    @Override
    public LocalDateTime parse(String parse) {
        return OffsetDateTime.parse(parse, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime();
    }
}
