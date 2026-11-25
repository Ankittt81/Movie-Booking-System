package com.system.design.moviebooking.ai.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.system.design.moviebooking.ai.ToolDefinition;
import com.system.design.moviebooking.dto.seatAndShow.SeatMapDto;
import com.system.design.moviebooking.exceptions.ResourceNotFound;
import com.system.design.moviebooking.security.AuthUser;
import com.system.design.moviebooking.service.IShowService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetSeatAvailabilityTool implements AiTool {

    private final IShowService showService;

    public GetSeatAvailabilityTool(IShowService showService) {
        this.showService = showService;
    }

    @Override
    public ToolDefinition definition() {
        Map<String, Object> schema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "showId", Map.of("type", "integer", "description", "The showId from search_shows")),
                "required", List.of("showId"));
        return new ToolDefinition("get_seat_availability",
                "Get how many seats are free for one show. Needs a showId from search_shows.",
                schema);
    }

    @Override
    public String execute(String argumentsJson, AuthUser user) {
        JsonNode args = ToolJson.parse(argumentsJson);
        long showId = (args == null) ? 0 : args.path("showId").asLong(0);
        if (showId < 1) {
            return ToolJson.error("showId is required and must be a positive number");
        }
        try {
            SeatMapDto map = showService.getSeatMap(showId);
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("showId", map.getShowId());
            result.put("movie", map.getMovieTitle());
            result.put("theatre", map.getTheatreName());
            result.put("startTime", map.getStartTime().toString());
            result.put("ticketPrice", map.getTicketPrice());
            result.put("totalSeats", map.getTotalSeats());
            result.put("availableSeats", map.getAvailableSeats());
            return ToolJson.write(result);
        } catch (ResourceNotFound e) {
            return ToolJson.error("Show not found");
        }
    }
}