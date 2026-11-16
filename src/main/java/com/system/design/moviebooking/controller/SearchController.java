package com.system.design.moviebooking.controller;

import com.system.design.moviebooking.dto.ApiResponse;
import com.system.design.moviebooking.dto.search.SearchResponseDto;
import com.system.design.moviebooking.service.SmartSearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final SmartSearchService searchService;

    public SearchController(SmartSearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<SearchResponseDto>> search(@RequestParam String q) {
        return ResponseEntity.ok(new ApiResponse<>("Search results", searchService.search(q)));
    }
}
