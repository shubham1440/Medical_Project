package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private String name;    // e.g., "Delhi"
    private String count;   // e.g., "2 Locations"
    private String image;   // e.g., "https://..."
}