package com.sibrahim.annoncify.services.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.sibrahim.annoncify.entity.Image;
import com.sibrahim.annoncify.repository.ImageRespository;
import com.sibrahim.annoncify.services.CloudVisionService;
import com.sibrahim.annoncify.services.ImageService;
import com.sibrahim.annoncify.services.LlamaApiClient;
import com.sibrahim.annoncify.services.ResponseParsingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class ImageServiceImpl implements ImageService {

    @Value("${firebase.storage.bucketName}")
    public String bucketName;

    private final ImageRespository imageRespository;

    InputStream inputStream = ImageServiceImpl.class.getClassLoader().getResourceAsStream("key.json");
    Credentials credentials = GoogleCredentials.fromStream(inputStream);

    public ImageServiceImpl(ImageRespository imageRespository) throws IOException {
        this.imageRespository = imageRespository;
    }

    @Override
    public String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of(bucketName, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }

    @Override
    public File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
        }
        return tempFile;
    }

    @Override
    public String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();                        // to get original file name
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));  // to generated random string values for file name.

            File file = this.convertToFile(multipartFile, fileName);                      // to convert multipartFile to File
            String URL = this.uploadFile(file, fileName);                                   // to get uploaded file link
            file.delete();
            return URL;
        } catch (Exception e) {
            e.printStackTrace();
            return "Image couldn't upload, Something went wrong";
        }
    }

    @Override
    public String deleteFileByUrl(String fileUrl) {
        try {
            Optional<Image> image = imageRespository.findByUrl(fileUrl);
            if (image.isPresent()){
                // Extract the file name from the URL
                String fileName = extractFileNameFromUrl(fileUrl);

                BlobId blobId = BlobId.of(bucketName, fileName);
                Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
                storage.delete(blobId);
                imageRespository.delete(image.get());
                return "File deleted successfully: " + fileName;
            }
            return "This Url Not Valid!";
        } catch (Exception e) {
            return "Error deleting file: " + fileUrl;
        }
    }

    private String extractFileNameFromUrl(String fileUrl) {
        // Extract the file name from the URL
        int lastSlashIndex = fileUrl.lastIndexOf("/");
        int questionMarkIndex = fileUrl.indexOf("?alt=media");

        // If ?alt=media is found in the URL, extract the file name up to that point
        if (questionMarkIndex != -1) {
            return fileUrl.substring(lastSlashIndex + 1, questionMarkIndex);
        } else {
            // If ?alt=media is not found, extract the file name up to the end
            return fileUrl.substring(lastSlashIndex + 1);
        }
    }

    public String uploadImageToFirebase(MultipartFile imageFile) {
        try {
            log.info("UPLOADING IMAGE TO FIREBASE: {}", imageFile.getOriginalFilename());
            String imageUrl = upload(imageFile);
            log.info("UPLOAD SUCCESSFUL: {} - URL: {}", imageFile.getOriginalFilename(), imageUrl);
            return imageUrl;
        } catch (Exception e) {
            log.error("FAILED TO UPLOAD IMAGE TO FIREBASE: {} - Error: {}", imageFile.getOriginalFilename(), e.getMessage());
            return null;
        }
    }

    public Map<String, String> generateCategoryAndSubcategory(List<MultipartFile> imgs) throws IOException {
        CloudVisionService cloudVisionService = new CloudVisionService();
        LlamaApiClient llamaApiClient = new LlamaApiClient(new ResponseParsingService());
        ExecutorService executor = Executors.newFixedThreadPool(5); // adjust the thread pool size as needed

        Map<String, String> categoryAndSubcategoryMap = new ConcurrentHashMap<>();
        List<String> allLabels = new ArrayList<>();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (int i = 0; i < 2 && i < imgs.size(); i++) {
            MultipartFile image = imgs.get(i);
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    byte[] imageBytes = image.getBytes();
                    List<String> labels = cloudVisionService.analyzeImage(imageBytes);
                    allLabels.addAll(labels);
                } catch (IOException e) {
                    log.error("Error processing image: {}", e.getMessage());
                }
            }, executor);

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Combine labels from multiple images into a single prompt
        String prompt = String.join(", ", allLabels);

        // Call Llama API with the combined prompt
        Map<String, String> categoryAndSubcategory = llamaApiClient.getCategoryAndSubcategory(prompt);

        // Create a new map with category and subcategory
        Map<String, String> result = new HashMap<>();
        result.put("category", categoryAndSubcategory.get("category"));
        result.put("subcategory", categoryAndSubcategory.get("subcategory"));

        executor.shutdown();

        cloudVisionService.close();

        return result;
    }
}
