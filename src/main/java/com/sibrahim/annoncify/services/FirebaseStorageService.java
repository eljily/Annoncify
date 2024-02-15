package com.sibrahim.annoncify.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FirebaseStorageService {

    String uploadImage(MultipartFile imageFile) throws IOException;

    String generateUniqueFilename(String originalFilename);

    String getFirebaseStorageUrl(String filename);
}
