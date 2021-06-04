package org.tests.yelpreview.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YelpBusiness {

    private String id;
    
    private String alias;
    
    private String name;
    
    @JsonProperty("image_url")
    private String imageUrl;
    
    private String url;
    
    private Double rating;
    
}
