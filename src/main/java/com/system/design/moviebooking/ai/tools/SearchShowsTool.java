package com.system.design.moviebooking.ai.tools;

import com.system.design.moviebooking.ai.ToolDefinition;
import com.system.design.moviebooking.dto.search.SearchResultDto;
import com.system.design.moviebooking.search.SearchFilterParser;
import com.system.design.moviebooking.search.SearchFilters;
import com.system.design.moviebooking.security.AuthUser;
import com.system.design.moviebooking.service.SmartSearchService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SearchShowsTool implements AiTool {

    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final int MAX_RESULTS = 10;

    private final SearchFilterParser parser;
    private final SmartSearchService searchService;

    public SearchShowsTool(SearchFilterParser parser, SmartSearchService searchService) {
        this.parser = parser;
        this.searchService = searchService;
    }

    @Override
    public ToolDefinition definition() {
        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("title", Map.of("type", "string", "description", "Part of the movie title"));
        properties.put("genre", Map.of("type", "string",
                "enum", List.of("Action", "Comedy", "Horror", "Love", "Patriot", "Bhakti", "Thriller", "Mystery")));
        properties.put("language", Map.of("type", "string",
                "enum", List.of("English", "Hindi", "Punjabi", "Telugu", "Bengali", "German")));
        properties.put("city", Map.of("type", "string"));
        properties.put("maxPrice", Map.of("type", "number", "description", "Maximum ticket price in rupees"));
        properties.put("day", Map.of("type", "string",
                "enum", List.of("TODAY", "TOMORROW", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                        "FRIDAY", "SATURDAY", "SUNDAY", "WEEKEND")));
        properties.put("timeOfDay", Map.of("type", "string",
                "enum", List.of("MORNING", "AFTERNOON", "EVENING", "NIGHT")));
        properties.put("maxDuration", Map.of("type", "number", "description", "Maximum movie length in minutes"));

        Map<String, Object> schema = Map.of("type", "object", "properties", properties);

        return new ToolDefinition("search_shows",
                "Search upcoming movie shows. Use only the fields the user mentioned. "
                        + "Returns up to 10 shows, each with a showId.",
                schema);
    }

    @Override
    public String execute(String argumentsJson, AuthUser user) {
        LocalDateTime now = LocalDateTime.now(ZONE);

        // the Module 2 parser validates everything and never throws
        SearchFilters filters = parser.parse(argumentsJson, now.toLocalDate());
        if (filters.isEmpty()) {
            return ToolJson.error("Provide at least one filter, for example genre, language, city or day");
        }

        List<SearchResultDto> found = searchService.findShows(filters, now);

        List<Map<String, Object>> rows = found.stream()
                .limit(MAX_RESULTS)
                .map(r -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("showId", r.getShowId());
                    row.put("movie", r.getMovieTitle());
                    row.put("genre", r.getGenre());
                    row.put("language", r.getLanguage());
                    row.put("theatre", r.getTheatreName());
                    row.put("city", r.getCity());
                    row.put("startTime", r.getStartTime().toString());
                    row.put("price", r.getTicketPrice());
                    return row;
                })
                .toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", found.size());
        result.put("shows", rows);
        return ToolJson.write(result);
    }
}