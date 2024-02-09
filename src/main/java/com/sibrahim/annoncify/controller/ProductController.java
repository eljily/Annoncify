package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.services.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> saveProduct(@RequestBody ProductDto productDto){
        try {
            return ResponseEntity.ok(productService.saveProduct(productDto));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProduct(){
        try {
            return ResponseEntity.ok(productService.getAllProducts());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id){
        try{
            return ResponseEntity.ok(productService.getProductById(id).get());
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(HttpStatus.valueOf(200));
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable Long id,@RequestBody ProductDto productDto){
        try {
            if(Objects.equals(id, productDto.getId())){
                return ResponseEntity.ok(productService.saveProduct(productDto));
            }
            return null;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return null;
        }
    }
}
