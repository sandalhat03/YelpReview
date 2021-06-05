package org.tests.yelpreview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YelpUser {
    
    private String id;
    
    @JsonProperty("profile_url")
    private String profileUrl;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    private String name;
}
