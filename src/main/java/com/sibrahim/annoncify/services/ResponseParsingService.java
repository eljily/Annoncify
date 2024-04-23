package com.sibrahim.annoncify.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service
@Slf4j
public class ResponseParsingService {

    private final ObjectMapper objectMapper;

    public ResponseParsingService() {
        this.objectMapper = new ObjectMapper();
    }

    public Map<String, String> parseResponse(String responseBody) {
        Map<String, String> categoryAndSubcategory = new HashMap<>();

        try {
            JsonNode responseNode = objectMapper.readTree(responseBody);
            JsonNode choicesNode = responseNode.get("choices");
            JsonNode messageNode = choicesNode.get(0).get("message");
            JsonNode functionCallNode = messageNode.get("function_call");
            String functionCallArguments = functionCallNode.get("arguments").asText();
            System.out.println("Function Call Arguments: " + functionCallArguments);

            // Use regex to extract the JSON object containing category and subcategory
            Pattern pattern = Pattern.compile("\\{.*\"category\".*\"subcategory\".*\\}");
            Matcher matcher = pattern.matcher(functionCallArguments);

            String category = "Unknown";
            String subcategory = "Unknown";

            if (matcher.find()) {
                String jsonMatch = matcher.group();
                JsonNode argumentsNode = objectMapper.readTree(jsonMatch);
                category = argumentsNode.get("category").asText();
                subcategory = argumentsNode.get("subcategory").asText();
            }

            // Put category and subcategory into the map
            categoryAndSubcategory.put("category", category);
            categoryAndSubcategory.put("subcategory", subcategory);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return categoryAndSubcategory;
    }
}