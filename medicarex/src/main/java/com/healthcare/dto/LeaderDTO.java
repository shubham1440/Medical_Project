package com.healthcare.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaderDTO {
    private String name;      // e.g., "Dr. Rajan Gopal"
    private String title;     // e.g., "Founder & Chairman"
    private String imageUrl;  // e.g., "https://..."
}
