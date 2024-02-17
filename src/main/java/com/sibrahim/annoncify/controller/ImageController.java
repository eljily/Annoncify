package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.services.impl.ImageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/app")
public class ImageController {

    private final ImageServiceImpl imageService;

    @PostMapping("/addImage")
    public String upload(@RequestParam("file") MultipartFile multipartFile) {
        return imageService.upload(multipartFile);
    }

    @PostMapping("/delete")
    public String delete(@RequestParam(name = "url") String url) {
        return imageService.deleteFileByUrl(url);
    }
}