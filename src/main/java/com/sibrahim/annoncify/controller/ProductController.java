package com.sibrahim.annoncify.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> saveProduct(@RequestBody ProductDto productDto){
        try {
            return ResponseEntity.ok(productService.saveProduct(productDto));
        }catch (Exception e){
            log.error("ERROR WHILE SAVING NEW PRODUCT "+e.getMessage());
            return null;
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProduct(){
        try {
            return ResponseEntity.ok(productService.getAllProducts());
        }catch (Exception e){
            log.error("ERROR WHILE GETTING ALL PRODUCTS,message:"+e.getMessage());
            return null;
        }
    }

    @PostMapping(value = "/addWithImages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> addProductWithImages(@RequestPart("product") String productJson,
                                                        @RequestPart("imageFiles") List<MultipartFile> imageFiles) {
        try {
            // Convert the JSON string to a Product object
            Product product = new ObjectMapper().readValue(productJson, Product.class);

            // Call your service method with the Product and imageFiles
            Product savedProduct = productService.addProductWithImages(product, imageFiles);

            if (savedProduct != null) {
                return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id){
        try{
            return ResponseEntity.ok(productService.getProductById(id).orElseThrow());
        }catch (Exception e){
            log.error("ERROR OCCURED WHILE FETCHING PRODUCT BY ID,message:"+e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(HttpStatus.valueOf(200));
        }catch (Exception e){
            log.error("ERROR OCCURED WHILE TRYING TO DELETE PRODUCT,message:"+e.getMessage());
            return null;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id, @RequestBody ProductDto productDto) {
        try {
            // Ensure that the id in the path matches the id in the request body
            if (!id.equals(productDto.getId())) {
                return ResponseEntity.badRequest().body(null);
            }

            ProductDto updatedProduct = productService.saveProduct(productDto);

            if (updatedProduct != null) {
                return ResponseEntity.ok(updatedProduct);
            } else {
                // Handle the case where the product with the given ID is not found
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            // Log the exception for debugging purposes
            log.error("ERROR OCCURED WHILE TRYING TO UPDATE PRODUCT,message:"+e.getMessage());
            // Return an appropriate HTTP status code and error message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
