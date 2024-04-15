package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.services.CloudVisionService;
import com.sibrahim.annoncify.services.impl.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
public class ImageController {

    private final ImageServiceImpl imageService;

    @Autowired
    private CloudVisionService cloudVisionService;

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
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new String[]{"Error: Failed to analyze image"});
        }
    }

    @PostMapping("/addImage")
    public String upload(@RequestParam("file") MultipartFile multipartFile) {
        return imageService.upload(multipartFile);
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(name = "url") String url) {
        return imageService.deleteFileByUrl(url);
    }
}