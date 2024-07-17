package com.sibrahim.annoncify.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.services.CloudVisionService;
import com.sibrahim.annoncify.services.LlamaApiClient;
import com.sibrahim.annoncify.services.impl.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageServiceImpl imageService;
    private final LlamaApiClient llamaApiClient;
    public Logger log = LoggerFactory.getLogger(ImageController.class);
    private final CloudVisionService cloudVisionService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeImage(@RequestParam("image") MultipartFile imageFile) {
        if (imageFile.isEmpty()) {
            return ResponseEntity.badRequest().body(new String[]{"Error: No image uploaded"});
        }

        try {
            byte[] imageBytes = imageFile.getBytes();
            List<?> categories = cloudVisionService.analyzeImage(imageBytes);
            return ResponseEntity.ok().body(categories);
        } catch (IOException e) {
            log.error("Exception Occurred : {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new String[]{"Error: Failed to analyze image"});
        }
    }
    @PostMapping
    public Map<?,?> upload(@RequestParam("file") MultipartFile multipartFile) {
        return Map.of("url",imageService.upload(multipartFile));
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(name = "url") String url) {
        return imageService.deleteFileByUrl(url);
    }

    @GetMapping
    public Object askLlm(@RequestParam String question) {
        return llamaApiClient.getCategoryAndSubcategory(question);
    }

    @PostMapping("/analyze-images")
    public ResponseEntity<Map<String, String>> analyzeImages(@RequestParam("images") List<MultipartFile> images) throws IOException {
        Map<String, String> categoryAndSubcategory = imageService.generateCategoryAndSubcategory(images);
        return ResponseEntity.ok(categoryAndSubcategory);
    }
}