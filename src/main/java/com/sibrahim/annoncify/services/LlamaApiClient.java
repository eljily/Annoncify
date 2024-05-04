package com.sibrahim.annoncify.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sibrahim.annoncify.util.ResponseParsingUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class LlamaApiClient {

    private static final String LLAMA_API_URL = "https://api.llama-api.com/chat/completions";
    private static final String API_KEY = "LL-9ZFA3iZ2gmXbx3Wu8m0nwqvyeI2YKiKr9M0qmIyFtWg85cg3ApyOicxWSNI5NQpi";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResponseParsingUtil responseParsingUtil;

    public LlamaApiClient(ResponseParsingUtil responseParsingUtil) {
        this.responseParsingUtil = responseParsingUtil;
    }

    public Map<String, String> getCategoryAndSubcategory(String labels) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("model", "llama3-70b");
        requestBody.put("stream", false);

        ObjectNode systemMessageObject = objectMapper.createObjectNode();
        systemMessageObject.put("role", "system");
        systemMessageObject.put("content", "Based on the labels, please determine which category and subcategory fit more from the following categories,and give response only based on them : Electronics (Mobile Phones & Tablets, Computers & Laptops, TVs & Home Theater Systems, Cameras & Photography Equipment), Real Estate (Apartments & Flats, Houses & Villas, Commercial Spaces, Land & Plots), Vehicles (Cars, Motorcycles, Trucks & Commercial Vehicles, Boats & Watercraft), Fashion & Accessories (Clothing, Shoes, Bags & Luggage, Watches & Jewelry),Other(subcategory: Other). Please return the response in the format: 'Category, Subcategory'.");

        ObjectNode userMessageObject = objectMapper.createObjectNode();
        userMessageObject.put("role", "user");
        userMessageObject.put("content", labels);

        ArrayNode messagesArray = objectMapper.createArrayNode();
        messagesArray.add(systemMessageObject);
        messagesArray.add(userMessageObject);
        requestBody.set("messages", messagesArray);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(LLAMA_API_URL, HttpMethod.POST, request, String.class);
            log.info("Response Body: {}", response.getBody());
            return responseParsingUtil.parseResponse(response.getBody());
        } catch (Exception e) {
            log.error("Error occurred while exchanging on restTemplate: {}", e.getMessage(), e);
            return null;
        }
    }
}