package com.sibrahim.annoncify.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@Slf4j
public class LlamaApiClient {

    private static final String LLAMA_API_URL = "https://api.llama-api.com/chat/completions";
    private static final String API_KEY = "LL-qmEA4G5RtEM5fxfT48hNnfYisCyk6eHBnSrpFVtdtYCdEySw1ye55H8RpMYt4GPL";

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResponseParsingService responseParsingService;

    public LlamaApiClient(ResponseParsingService responseParsingService) {
        this.responseParsingService = responseParsingService;
    }

    public Map<String, String> getCategoryAndSubcategory(String labels) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + API_KEY);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ObjectNode requestBody = objectMapper.createObjectNode();
        requestBody.put("stream", false);

        ObjectNode messageObject = objectMapper.createObjectNode();
        messageObject.put("role", "user");
        messageObject.put("content", "What is the category and subcategory of a product that is more likely to be described by the labels please return only the number : " + labels + "? If it doesn't fit into one of the following categories, please return 'Other' for both category and subcategory. Categories: Electronics, Fashion, Real Estate, Vehicles,Fashion & Accessories. Subcategories: Apartments & Flats, Houses & Villas, Commercial Spaces, Land & Plots, Cars, Motorcycles, Trucks & Commercial Vehicles, Boats & Watercraft, Mobile Phones & Tablets, Computers & Laptops, TVs & Home Theater Systems, Cameras & Photography Equipment, Clothing, Shoes, Bags & Luggage, Watches & Jewelry, Other. Please return the response in JSON format: {category: '..', subcategory: '..'}");

        requestBody.set("messages", objectMapper.createArrayNode().add(messageObject));

        ArrayNode functionsArray = objectMapper.createArrayNode();
        ObjectNode functionObject = objectMapper.createObjectNode();
        functionObject.put("name", "get_category_and_subcategory");
        ObjectNode parametersObject = objectMapper.createObjectNode();
        parametersObject.put("type", "object");
        ObjectNode propertiesObject = objectMapper.createObjectNode();
        ObjectNode labelsObject = objectMapper.createObjectNode();
        labelsObject.put("type", "array");
        ObjectNode itemsObject = objectMapper.createObjectNode();
        itemsObject.put("type", "string");
        labelsObject.set("items", itemsObject);
        propertiesObject.set("labels", labelsObject);
        parametersObject.set("properties", propertiesObject);
        functionObject.set("parameters", parametersObject);
        functionObject.set("required", objectMapper.createArrayNode().add("labels"));
        functionsArray.add(functionObject);
        requestBody.set("functions", functionsArray);

        HttpEntity<String> request = new HttpEntity<>(requestBody.toString(), headers);

        ResponseEntity<String> response = restTemplate.exchange(LLAMA_API_URL, HttpMethod.POST, request, String.class);
        log.info("Response Body: {}", response.getBody());

        return responseParsingService.parseResponse(response.getBody());
    }
}
