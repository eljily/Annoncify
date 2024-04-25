package com.sibrahim.annoncify.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
@Slf4j
public class ResponseParsingUtil {

    private final ObjectMapper objectMapper;

    public ResponseParsingUtil() {
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, String> parseResponse(String responseBody) {
        Map<String, String> categoryAndSubcategory = new HashMap<>();

        try {
            JsonNode responseNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = responseNode.get("choices");
            JsonNode messageNode = choicesNode.get(0).get("message");
            String content = messageNode.get("content").asText();

            // Extract category and subcategory from the content
            Pattern pattern = Pattern.compile("Category: (.*)\nSubcategory: (.*)");
            Matcher matcher = pattern.matcher(content);

            if (matcher.find()) {
                String category = matcher.group(1).trim();
                String subcategory = matcher.group(2).trim();

                // Check if the category and subcategory are from the provided ones
                String[] categories = {"Electronics", "Real Estate", "Vehicles", "Fashion & Accessories","Other"};
                String[] subcategories = {"Mobile Phones & Tablets",
                        "Computers & Laptops", "TVs & Home Theater Systems",
                        "Cameras & Photography Equipment", "Apartments & Flats",
                        "Houses & Villas", "Commercial Spaces", "Land & Plots",
                        "Cars", "Motorcycles", "Trucks & Commercial Vehicles",
                        "Boats & Watercraft", "Clothing", "Shoes", "Bags & Luggage",
                        "Watches & Jewelry", "Building", "Other"};

                if (Arrays.asList(categories).contains(category) && Arrays.asList(subcategories).contains(subcategory)) {
                    categoryAndSubcategory.put("category", category);
                    categoryAndSubcategory.put("subcategory", subcategory);
                } else {
                    categoryAndSubcategory.put("category", "Other");
                    categoryAndSubcategory.put("subcategory", "Other");
                }
            } else {
                // Split the content by comma to get category and subcategory
                String[] parts = content.split(", ");
                String category = parts[0].trim();
                String subcategory = parts[1].trim();

                categoryAndSubcategory.put("category", category);
                categoryAndSubcategory.put("subcategory", subcategory);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryAndSubcategory;
    }
}