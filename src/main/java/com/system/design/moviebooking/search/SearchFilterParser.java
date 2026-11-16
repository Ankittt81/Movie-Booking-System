package com.system.design.moviebooking.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
public class SearchFilterParser {
    private static final Logger log = LoggerFactory.getLogger(SearchFilterParser.class);
    private static final List<String> GENRES=List.of("Action", "Comedy", "Horror", "Love", "Patriot", "Bhakti", "Thriller", "Mystery");
    private static final List<String> LANGUAGES=List.of("English", "Hindi", "Punjabi", "Telugu", "Bengali", "German");
    private static final double MAX_PRICE_LIMIT = 100000;
    private static final int MAX_DAYS_AHEAD = 14;
    private static final int MAX_TITLE_LENGTH = 100;
    private static final int MAX_CITY_LENGTH = 60;

    private final ObjectMapper objectMapper;

    public SearchFilterParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    // Never throws: any problem gives an empty SearchFilters.
    public SearchFilters parse(String modelText, LocalDate today) {
        RawFilters raw = readJson(modelText);
        if (raw == null) {
            return empty();
        }
        return new SearchFilters(
                cleanText(raw.getTitle(), MAX_TITLE_LENGTH),
                canonical(raw.getGenre(), GENRES),
                canonical(raw.getLanguage(), LANGUAGES),
                cleanText(raw.getCity(), MAX_CITY_LENGTH),
                cleanPrice(raw.getMaxPrice()),
                resolveDay(raw.getDay(), today),
                parseTimeOfDay(raw.getTimeOfDay()));
    }

    // ---------- 1. get the JSON out of the text ----------

    private RawFilters readJson(String text) {
        if (text == null) {
            return null;
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start < 0 || end <= start) {
            return null;
        }
        try {
            return objectMapper.readValue(text.substring(start, end + 1), RawFilters.class);
        } catch (JsonProcessingException e) {
            log.warn("Could not parse AI filters: {}", e.getOriginalMessage());
            return null;
        }
    }

    // ---------- 2. validate each field ----------

    private String cleanText(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty() || trimmed.equalsIgnoreCase("null")) {   // models sometimes send the word "null"
            return null;
        }
        return trimmed.length() > maxLength ? trimmed.substring(0, maxLength) : trimmed;
    }

    // returns the canonical spelling from the allowed list, or null
    private String canonical(String value, List<String> allowed) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        for (String candidate : allowed) {
            if (candidate.equalsIgnoreCase(trimmed)) {
                return candidate;
            }
        }
        return null;
    }

    private BigDecimal cleanPrice(Double price) {
        if (price == null || price.isNaN() || price <= 0 || price > MAX_PRICE_LIMIT) {
            return null;
        }
        return BigDecimal.valueOf(price);
    }

    private TimeOfDay parseTimeOfDay(String value) {
        if (value == null) {
            return null;
        }
        try {
            return TimeOfDay.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // ---------- 3. turn the day keyword into a real date (Java does the calendar maths) ----------

    private LocalDate resolveDay(String day, LocalDate today) {
        if (day == null) {
            return null;
        }
        String key = day.trim().toUpperCase();
        LocalDate date = switch (key) {
            case "TODAY" -> today;
            case "TOMORROW" -> today.plusDays(1);
            case "WEEKEND" -> weekend(today);
            default -> weekday(key, today);
        };
        if (date == null || date.isAfter(today.plusDays(MAX_DAYS_AHEAD))) {
            return null;
        }
        return date;
    }

    private LocalDate weekend(LocalDate today) {
        DayOfWeek dow = today.getDayOfWeek();
        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return today;
        }
        return today.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
    }

    // MONDAY..SUNDAY: the next such day, today included; null for anything else
    private LocalDate weekday(String key, LocalDate today) {
        try {
            return today.with(TemporalAdjusters.nextOrSame(DayOfWeek.valueOf(key)));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private SearchFilters empty() {
        return new SearchFilters(null, null, null, null, null, null, null);
    }
}