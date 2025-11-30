package com.system.design.moviebooking.model;


import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Artist extends BaseModel{
    private String name;
}
