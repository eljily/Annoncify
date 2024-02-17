package com.sibrahim.annoncify.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public interface ImageService {

    String uploadFile(File file, String fileName) throws IOException;
    File convertToFile(MultipartFile multipartFile, String fileName) throws IOException;
    String getExtension(String fileName);
    String upload(MultipartFile multipartFile);
    String deleteFileByUrl(String fileUrl);
}
