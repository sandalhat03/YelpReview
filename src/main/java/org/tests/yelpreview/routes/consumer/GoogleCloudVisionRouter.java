package org.tests.yelpreview.routes.consumer;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tests.yelpreview.model.EmotionLikelihood;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.FaceAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;

@Component
public class GoogleCloudVisionRouter extends RouteBuilder {

    public static final String GOOGLE_CLOUD_VISION = "direct:googleCloudVision";

    @Override
    public void configure() throws Exception {
        from(GOOGLE_CLOUD_VISION).id("GoogleCloudVisionRouter")
                                 .doTry()
                                     .process(new GoogleCloudVissionProcessor())
                                 .doCatch(Exception.class)
                                     .process(e -> {
                                         e.getMessage().setBody(new ArrayList<EmotionLikelihood>());
                                     });
    }
    
    /**
     * Retrieve joy and sorrow likelihood of image
     */
    private static class GoogleCloudVissionProcessor implements Processor {

        @Override
        public void process(Exchange e) throws Exception {
            List<AnnotateImageRequest> requests = new ArrayList<>();

            String imagePath = e.getMessage()
                                .getBody(String.class);

            AnnotateImageRequest request = createRequest(imagePath);
            requests.add(request);

            List<EmotionLikelihood> emotions = new ArrayList<>();

            try (ImageAnnotatorClient client = ImageAnnotatorClient.create()) {
                BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
                List<AnnotateImageResponse> responses = response.getResponsesList();

                for (AnnotateImageResponse res : responses) {
                    if (res.hasError()) {
                        System.out.format("Error: %s%n", res.getError()
                                                            .getMessage());
                        return;
                    }

                    // For full list of available annotations, see http://g.co/cloud/vision/docs
                    for (FaceAnnotation annotation : res.getFaceAnnotationsList()) {
                        emotions.add(new EmotionLikelihood(annotation.getJoyLikelihood()
                                                                     .toString(),
                                annotation.getSorrowLikelihood()
                                          .toString()));
                    }
                }
            }
            
            e.getMessage().setBody(emotions);
        }

        /**
         * Construct AnnotateImageRequest instance.
         * @param imagePath
         * @return return AnnotateImageRequest instance.
         */
        private AnnotateImageRequest createRequest(String imagePath) {
            ImageSource imgSource = ImageSource.newBuilder()
                                               .setImageUri(imagePath)
                                               .build();
            Image img = Image.newBuilder()
                             .setSource(imgSource)
                             .build();
            Feature feat = Feature.newBuilder()
                                  .setType(Feature.Type.FACE_DETECTION)
                                  .build();

            AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
                                                               .addFeatures(feat)
                                                               .setImage(img)
                                                               .build();
            return request;
        }
        
    }

}
