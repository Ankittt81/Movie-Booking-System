package com.system.design.moviebooking.service;

import com.system.design.moviebooking.ai.*;
import com.system.design.moviebooking.dto.search.SearchResponseDto;
import com.system.design.moviebooking.dto.search.SearchResultDto;
import com.system.design.moviebooking.exceptions.BusinessException;
import com.system.design.moviebooking.model.mainEntity.Show;
import com.system.design.moviebooking.repository.ShowRepository;
import com.system.design.moviebooking.search.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class SmartSearchService {

    private static final Logger log = LoggerFactory.getLogger(SmartSearchService.class);
    private static final ZoneId ZONE = ZoneId.of("Asia/Kolkata");
    private static final int MAX_QUERY_LENGTH = 200;
    private static final int FETCH_LIMIT = 50;
    private static final int RESULT_LIMIT = 30;

    private final AiClient aiClient;
    private final SearchPromptBuilder promptBuilder;
    private final SearchFilterParser parser;
    private final ShowRepository showRepository;

    public SmartSearchService(AiClient aiClient, SearchPromptBuilder promptBuilder,
                              SearchFilterParser parser, ShowRepository showRepository) {
        this.aiClient = aiClient;
        this.promptBuilder = promptBuilder;
        this.parser = parser;
        this.showRepository = showRepository;
    }

    // No @Transactional here on purpose: a database connection must not be held during the AI call.
    public SearchResponseDto search(String rawQuery) {
        String query=(rawQuery==null)?"":rawQuery.trim();
        if (query.isEmpty() || query.length() > MAX_QUERY_LENGTH) {
            throw new BusinessException("Search text must be 1 to " + MAX_QUERY_LENGTH + " characters");
        }
        LocalDateTime now = LocalDateTime.now(ZONE);

        SearchFilters aiFilters = askAi(query, now);
        boolean aiUsed = !aiFilters.isEmpty();
        // fallback: AI failed or understood nothing, so search the movie title with the raw text
        SearchFilters filters = aiUsed ? aiFilters : new SearchFilters(query, null, null, null, null, null, null,null);

        List<SearchResultDto> results=findShows(filters, now);

        return new SearchResponseDto(filters, aiUsed, results);
    }

    public List<SearchResultDto> findShows(SearchFilters filters, LocalDateTime now){
        List<SearchResultDto> results = showRepository
                .findAll(ShowSpecifications.matching(filters, now),
                        PageRequest.of(0, FETCH_LIMIT, Sort.by("startTime")))
                .stream()
                .filter(s -> filters.getTimeOfDay() == null || inTimeOfDay(s.getStartTime(), filters.getTimeOfDay()))
                .limit(RESULT_LIMIT)
                .map(this::toDto)
                .toList();
        return results;
    }

    private SearchFilters askAi(String query, LocalDateTime now) {
        try {
            AiResponse response = aiClient.chat(new AiRequest(
                    promptBuilder.build(now.toLocalDate(), now.toLocalTime()),
                    List.of(new ChatMessage(Role.USER, query)),
                    0.0,
                    1000));
            return parser.parse(response.getText(), now.toLocalDate());
        } catch (AiException e) {
            log.warn("AI search failed, falling back to title search: {}", e.getMessage());
            return new SearchFilters(null, null, null, null, null, null, null,null);
        }
    }

    private boolean inTimeOfDay(LocalDateTime start, TimeOfDay timeOfDay) {
        int hour = start.getHour();
        return switch (timeOfDay) {
            case MORNING -> hour >= 5 && hour < 12;
            case AFTERNOON -> hour >= 12 && hour < 17;
            case EVENING -> hour >= 17 && hour < 21;
            case NIGHT -> hour >= 21 || hour < 5;
        };
    }

    private SearchResultDto toDto(Show s) {
        return new SearchResultDto(
                s.getId(), s.getStartTime(), s.getTicketPrice(),
                s.getMovie().getId(), s.getMovie().getTitle(), s.getMovie().getPoster(),
                s.getMovie().getGenre(), s.getMovie().getLanguage(),
                s.getTheatre().getId(), s.getTheatre().getName(), s.getTheatre().getCity());
    }
}