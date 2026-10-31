package com.system.design.moviebooking.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TheatreShowsDto {
   private Long  theatreId;
   private String theatreName;
   private String address;
   private String city;
   private List<ShowSummaryDto> shows;
}
