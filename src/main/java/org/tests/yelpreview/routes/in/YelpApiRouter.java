package org.tests.yelpreview.routes.in;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.http.base.HttpOperationFailedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tests.yelpreview.model.YelpBusiness;

import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class YelpApiRouter extends RouteBuilder {

    public static final String YELP_SEARCH = "direct:yelpSearch";

    public static final String YELP_REVIEWS = "direct:yelpReviews";

    @Value("${yelp.api.key}")
    private String apiKey;

    @Value("${yelp.api.url}")
    private String apiUrl;

    @Value("${yelp.api.base-path}")
    private String apiBasePath;

    @Override
    public void configure() throws Exception {

        onExceptionHandling();
        
        restConfiguration().host("api.yelp.com");

        JacksonDataFormat yelpBusinessFormat = new JacksonDataFormat(YelpBusiness.class);
        yelpBusinessFormat.disableFeature(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        
        from(YELP_SEARCH).id("YelpApiRouter.YELP_SEARCH")
                         .setHeader("Authorization", constant("Bearer " + apiKey))
                         .to(apiUrl + apiBasePath + "/search")
                         .convertBodyTo(String.class)
                         .unmarshal(yelpBusinessFormat)
                         .end();

        from(YELP_REVIEWS).id("YelpApiRouter.YELP_REVIEWS")
                          .setHeader("Authorization", constant("Bearer " + apiKey))
                          .to(apiUrl + apiBasePath + "/reviews")
                          .convertBodyTo(String.class)
                          .unmarshal(yelpBusinessFormat)
                          .log("${body}");

    }
    
    private void onExceptionHandling() {

        onException(HttpOperationFailedException.class).setBody(constant("ERROR"))
                                                       .continued(true);

        
    }
}
