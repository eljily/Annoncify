package com.sibrahim.annoncify.services.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.sibrahim.annoncify.services.FirebaseStorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class FirebaseStorageServiceImpl implements FirebaseStorageService {

    @Value("${firebase.storage.bucketName}")
    public String bucketName;

    @Override
    public String uploadImage(MultipartFile imageFile) throws IOException {
        // Generate a unique filename for the image
        String filename = generateUniqueFilename(Objects.requireNonNull(imageFile.getOriginalFilename()));

        // Upload the image to Firebase Storage
        Storage storage = StorageOptions.newBuilder().setProjectId(bucketName).build().getService();
        BlobId blobId = BlobId.of(bucketName, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, imageFile.getBytes());

        // Get the URL of the uploaded image

        return getFirebaseStorageUrl(filename);
    }

    @Override
    public String generateUniqueFilename(String originalFilename) {
        // Generate a unique filename using UUID to avoid collisions
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        return UUID.randomUUID().toString() + extension;
    }

    @Override
    public String getFirebaseStorageUrl(String filename) {
        // Construct the URL of the uploaded image in Firebase Storage
        return "https://storage.googleapis.com/" + bucketName + "/" + filename;
    }
}
