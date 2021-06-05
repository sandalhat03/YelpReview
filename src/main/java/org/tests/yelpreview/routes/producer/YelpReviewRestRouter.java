package org.tests.yelpreview.routes.producer;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.tests.yelpreview.routes.consumer.YelpApiRouter;
import org.tests.yelpreview.routes.service.YelpServiceRouter;

@Component
public class YelpReviewRestRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        restConfiguration().component("netty-http")
                           .host("0.0.0.0")
                           .port(8084);

        rest("/yelp/reviews").get("/{id}/")
                             .id("YelpReviewRouter")
                             .to(YelpServiceRouter.YELP_PROCESSING);

    }

}
