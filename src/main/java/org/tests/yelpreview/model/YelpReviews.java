package org.tests.yelpreview.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class YelpReviews {

    private List<YelpReview> reviews;

    private YelpError error;
    
}
