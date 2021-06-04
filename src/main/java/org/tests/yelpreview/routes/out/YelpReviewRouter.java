package org.tests.yelpreview.routes.out;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.tests.yelpreview.routes.in.YelpApiRouter;

@Component
public class YelpReviewRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("timer://foo?fixedRate=true&period=5000").id("YelpReviewRouter")
                                                      .to(YelpApiRouter.YELP_REVIEWS);

    }

}
