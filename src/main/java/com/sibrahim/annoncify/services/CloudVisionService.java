package com.sibrahim.annoncify.services;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.vision.v1.*;

import com.google.protobuf.ByteString;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//@Service
public class CloudVisionService {

//    private final ImageAnnotatorClient visionClient;
//
//    public CloudVisionService() throws IOException {
//        // Load credentials from key.json file in resources directory
//        Resource credentialsFile = new ClassPathResource("service_key.json");
//        InputStream inputStream = credentialsFile.getInputStream();
//        Credentials credentials = GoogleCredentials.fromStream(inputStream);
//        inputStream.close();
//
//        // Initialize Vision API client using the loaded credentials
//        this.visionClient = ImageAnnotatorClient.create(ImageAnnotatorSettings.newBuilder().setCredentialsProvider(FixedCredentialsProvider.create(credentials)).build());
//    }
//
//    // Method to analyze image and extract category and subcategory
//    public List<String> analyzeImage(byte[] imageBytes) throws IOException {
//        Image image = Image.newBuilder().setContent(ByteString.copyFrom(imageBytes)).build();
//        AnnotateImageRequest request = AnnotateImageRequest.newBuilder()
//                .addFeatures(Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION))
//                .setImage(image)
//                .build();
//
//        BatchAnnotateImagesResponse response = visionClient.batchAnnotateImages(Collections.singletonList(request));
//        List<AnnotateImageResponse> responses = response.getResponsesList();
//
//        List<String> detectedLabels = new ArrayList<>();
//
//        // Loop through the responses
//        for (AnnotateImageResponse res : responses) {
//            // Loop through the label annotations
//            for (EntityAnnotation annotation : res.getLabelAnnotationsList()) {
//                String label = annotation.getDescription();
//                detectedLabels.add(label);
//            }
//        }
//
//        return detectedLabels;
//    }
//
//
//    public void close() {
//        visionClient.close();
//    }
}