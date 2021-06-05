package org.tests.yelpreview.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YelpReview {

    private String id;
    
    private String url;
    
    private String text;
    
    private Double rating;
    
    @JsonProperty("time_created")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-dd-MM HH:mm:ss")
    private Date timeCreated;
    
    private YelpUser user;
    
    private List<EmotionLikelihood> emotions;

}
