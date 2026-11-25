package com.system.design.moviebooking.ai.tools;

import com.system.design.moviebooking.ai.ToolDefinition;
import com.system.design.moviebooking.dto.booking.BookingResponseDto;
import com.system.design.moviebooking.security.AuthUser;
import com.system.design.moviebooking.service.IBookingService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class GetMyBookingsTool implements AiTool {

    private final IBookingService bookingService;

    public GetMyBookingsTool(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Override
    public ToolDefinition definition() {
        Map<String, Object> schema = Map.of("type", "object", "properties", Map.of());
        return new ToolDefinition("get_my_bookings",
                "List the logged-in user's 5 most recent bookings. Takes no arguments.",
                schema);
    }

    @Override
    public String execute(String argumentsJson, AuthUser user) {
        if (user == null) {
            return ToolJson.error("Not logged in");
        }
        // the id comes ONLY from the logged-in user, never from the model's arguments
        List<BookingResponseDto> bookings = bookingService.getMyBookings(
                user.getId(), PageRequest.of(0, 5, Sort.by("id").descending()));

        List<Map<String, Object>> rows = bookings.stream().map(b -> {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("bookingId", b.getBookingId());
            row.put("movie", b.getMovieTitle());
            row.put("theatre", b.getTheatreName());
            row.put("startTime", b.getStartTime().toString());
            row.put("seats", b.getSeatLabels());
            row.put("status", b.getStatus().name());
            row.put("totalAmount", b.getTotalAmount());
            return row;
        }).toList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("count", rows.size());
        result.put("bookings", rows);
        return ToolJson.write(result);
    }
}