package org.tests.yelpreview.routes.service;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.tests.yelpreview.model.YelpError;
import org.tests.yelpreview.model.YelpReview;
import org.tests.yelpreview.model.YelpReviews;
import org.tests.yelpreview.routes.consumer.GoogleCloudVisionRouter;
import org.tests.yelpreview.routes.consumer.YelpApiRouter;

@Component
public class YelpServiceRouter extends RouteBuilder {

    public static final String YELP_PROCESSING = "direct:yelpProcessing";

    @SuppressWarnings("unchecked")
    @Override
    public void configure() throws Exception {
        from(YELP_PROCESSING).id("YelpServiceRouter")
                             .doTry()
                             .to(YelpApiRouter.YELP_REVIEWS)
                             .split(simple("${body.reviews}"))
                                 .setProperty("review", body())
                                 .process(e -> {
                                     YelpReview yelpReview = e.getMessage().getBody(YelpReview.class);
                                     e.getMessage().setBody(yelpReview.getUser().getImageUrl());  
                                 })
                                 .to(GoogleCloudVisionRouter.GOOGLE_CLOUD_VISION)
                                 .process(e -> {
                                     YelpReview yelpReview = (YelpReview) e.getAllProperties().get("review");
                                     yelpReview.setEmotions(e.getMessage().getBody(List.class));
                                 })
                             .end()
                             .endDoTry()
                             .doCatch(Exception.class)
                                 .process(e -> {
                                     YelpReviews yelpReviews = new YelpReviews();
                                     yelpReviews.setError(new YelpError("SYSTEM_ERROR", "Unexpected error"));
                                     e.getMessage()
                                      .setBody(yelpReviews);
                                 })
                             .end()
                             .marshal()
                             .json();

    }

}
