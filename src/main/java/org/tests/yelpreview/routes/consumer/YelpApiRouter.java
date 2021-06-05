package org.tests.yelpreview.routes.consumer;

import javax.annotation.PostConstruct;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tests.yelpreview.model.YelpError;
import org.tests.yelpreview.model.YelpReviews;

import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class YelpApiRouter extends RouteBuilder {

    public static final String YELP_REVIEWS = "direct:yelpReviews";

    @Value("${yelp.api.key}")
    private String apiKey;

    @Value("${yelp.api.url}")
    private String apiUrl;

    @Value("${yelp.api.base-path}")
    private String apiBasePath;

    private JacksonDataFormat yelpReviewsFormat;

    @Override
    public void configure() throws Exception {

        from(YELP_REVIEWS).id("YelpApiRouter.YELP_REVIEWS")
                          .doTry()
                              .removeHeader(Exchange.HTTP_PATH)
                              .setHeader("Authorization", constant("Bearer " + apiKey))
                              .toD(apiUrl + apiBasePath + "/${header.id}/reviews?bridgeEndpoint=true")
                              .convertBodyTo(String.class)
                              .unmarshal(yelpReviewsFormat)
                          .doCatch(HttpOperationFailedException.class)
                              .process(e -> {
                                  final HttpOperationFailedException ex = e.getProperty(Exchange.EXCEPTION_CAUGHT, HttpOperationFailedException.class);
                                  e.getMessage()
                                   .setBody(ex.getResponseBody());
                              })
                              .unmarshal(yelpReviewsFormat)
                          .doCatch(Exception.class)
                              .process(e -> {
                                  YelpReviews yelpReviews = new YelpReviews();
                                  yelpReviews.setError(new YelpError("SYSTEM_ERROR", "Unexpected error"));
                                  e.getMessage()
                                   .setBody(yelpReviews);
                              })
                          .end();

    }

    @PostConstruct
    private void init() {

        yelpReviewsFormat = new JacksonDataFormat(YelpReviews.class);
        yelpReviewsFormat.disableFeature(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

}
