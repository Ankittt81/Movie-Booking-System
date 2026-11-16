package com.system.design.moviebooking.dto.search;

import com.system.design.moviebooking.search.SearchFilters;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResponseDto {
    private SearchFilters filters;       // what the AI understood, so the UI can show it
    private boolean aiUsed;              // false means plain title search was used
    private List<SearchResultDto> results;
}
