package com.system.design.moviebooking.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class SearchFilterParserTest {

    // Tuesday
    private static final LocalDate TODAY = LocalDate.of(2026, 11, 17);

    private final SearchFilterParser parser = new SearchFilterParser(new ObjectMapper());

    @Test
    void parsesFullValidReply() {
        String json = "{\"title\":null,\"genre\":\"Comedy\",\"language\":\"Hindi\",\"city\":\"Noida\","
                + "\"maxPrice\":300,\"day\":\"TODAY\",\"timeOfDay\":\"EVENING\"}";

        SearchFilters f = parser.parse(json, TODAY);

        assertThat(f.getGenre()).isEqualTo("Comedy");
        assertThat(f.getLanguage()).isEqualTo("Hindi");
        assertThat(f.getCity()).isEqualTo("Noida");
        assertThat(f.getMaxPrice()).isEqualByComparingTo(new BigDecimal("300"));
        assertThat(f.getDate()).isEqualTo(TODAY);
        assertThat(f.getTimeOfDay()).isEqualTo(TimeOfDay.EVENING);
        assertThat(f.getTitle()).isNull();
    }

    @Test
    void invalidGenreBecomesNullAndSaturdayIsResolved() {
        SearchFilters f = parser.parse("{\"genre\":\"Rom-com\",\"day\":\"SATURDAY\"}", TODAY);

        assertThat(f.getGenre()).isNull();
        assertThat(f.getDate()).isEqualTo(LocalDate.of(2026, 11, 21));
    }

    @Test
    void stripsCodeFences() {
        String reply = "```json\n{\"day\":\"TOMORROW\"}\n```";

        assertThat(parser.parse(reply, TODAY).getDate()).isEqualTo(LocalDate.of(2026, 11, 18));
    }

    @Test
    void garbageGivesEmptyFilters() {
        assertThat(parser.parse("not json at all", TODAY).isEmpty()).isTrue();
        assertThat(parser.parse("{ broken json", TODAY).isEmpty()).isTrue();
        assertThat(parser.parse(null, TODAY).isEmpty()).isTrue();
    }

    @Test
    void negativePriceDroppedAndWeekendResolved() {
        SearchFilters f = parser.parse("{\"maxPrice\":-5,\"day\":\"WEEKEND\"}", TODAY);

        assertThat(f.getMaxPrice()).isNull();
        assertThat(f.getDate()).isEqualTo(LocalDate.of(2026, 11, 21));
    }

    @Test
    void unknownDayKeywordGivesNullDate() {
        assertThat(parser.parse("{\"day\":\"TONIGHT\"}", TODAY).getDate()).isNull();
    }

    @Test
    void dateTooFarAheadIsDropped() {
        // next Monday is 6 days away (allowed), but a keyword can never go past 14 days
        assertThat(parser.parse("{\"day\":\"MONDAY\"}", TODAY).getDate())
                .isEqualTo(LocalDate.of(2026, 11, 23));
    }

    @Test
    void stringNullAndBlankTextBecomeNull() {
        SearchFilters f = parser.parse("{\"title\":\"null\",\"city\":\"   \"}", TODAY);

        assertThat(f.getTitle()).isNull();
        assertThat(f.getCity()).isNull();
        assertThat(f.isEmpty()).isTrue();
    }
}