package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.PaginationData;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.ProductRequestDto;
import com.sibrahim.annoncify.dto.ResponseMessage;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.services.FavoriteService;
import com.sibrahim.annoncify.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final Logger log = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final FavoriteService favoriteService;

    public ProductController(ProductService productService, FavoriteService favoriteService) {
        this.productService = productService;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getAllProduct(@RequestParam(name = "page", defaultValue = "0") int page,
                                                         @RequestParam(name = "size", defaultValue = "5") int size) {
        Page<ProductDto> products = productService.getAllProducts(page, size);
        PaginationData paginationData = new PaginationData(products);
        return ResponseEntity.ok(ResponseMessage.builder()
                .status(HttpStatus.OK.value())
                .message("Product Page Retrieved Successfully")
                .data(products.getContent())
                .meta(paginationData)
                .build());
    }

    @GetMapping("/productsBySubCategoryId/{categoryId}")
    public ResponseEntity<ResponseMessage> getAllProducts(@RequestParam(name = "page", defaultValue = "0") int page,
                                                          @RequestParam(name = "size", defaultValue = "14") int size,
                                                          @PathVariable(name = "categoryId") int categoryId) {
        Page<ProductDto> products = productService.getAllProducts(page, size, categoryId);
        PaginationData paginationData = new PaginationData(products);
        return ResponseEntity.ok(ResponseMessage.builder()
                .status(HttpStatus.OK.value())
                .message("Product Page Retrieved Successfully")
                .data(products.getContent())
                .meta(paginationData)
                .build());
    }

    @GetMapping("/productsByCategoryId/{categoryId}")
    public ResponseEntity<ResponseMessage> getAllProductsByCategoryID(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                      @RequestParam(name = "size", defaultValue = "14") int size,
                                                                      @PathVariable(name = "categoryId") int categoryId) {
        Page<ProductDto> products = productService.getAllProductsByCategoryId(page, size, categoryId);
        PaginationData paginationData = new PaginationData(products);
        return ResponseEntity.ok(ResponseMessage.builder()
                .status(HttpStatus.OK.value())
                .message("Product Page Retrieved Successfully")
                .data(products.getContent())
                .meta(paginationData)
                .build());
    }

    @GetMapping("/productsByKeyword/{keyword}")
    public ResponseEntity<ResponseMessage> getAllProductsByKeyWord(@RequestParam(name = "page", defaultValue = "0") int page,
                                                                   @RequestParam(name = "size", defaultValue = "14") int size,
                                                                   @PathVariable(name = "keyword") String keyword) {
        Page<ProductDto> products = productService.getProductsByKeyword(page, size, keyword);
        PaginationData paginationData = new PaginationData(products);
        return ResponseEntity.ok(ResponseMessage.builder()
                .status(HttpStatus.OK.value())
                .message("Product Page Retrieved Successfully")
                .data(products.getContent())
                .meta(paginationData)
                .build());
    }


    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseMessage> addProduct(@ModelAttribute ProductRequestDto product) {
        try {
            ProductDto savedProduct = productService.addProduct(product);

            ResponseMessage responseMessage = (savedProduct != null)
                    ? ResponseMessage.builder()
                    .message("product added")
                    .status(HttpStatus.OK.value())
                    .data(savedProduct)
                    .build()
                    : ResponseMessage.builder()
                    .message("Internal Server Error")
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .build();

            return ResponseEntity.status(savedProduct != null ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseMessage);
        } catch (Exception e) {
            return ResponseEntity.ofNullable(ResponseMessage.builder().message(e.getMessage()).status(500).build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getProductById(id).orElseThrow());
        } catch (Exception e) {
            log.error("ERROR OCCURRED WHILE FETCHING PRODUCT BY ID,message:" + e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(HttpStatus.valueOf(200));
        } catch (Exception e) {
            log.error("ERROR OCCURRED WHILE TRYING TO DELETE PRODUCT,message:" + e.getMessage());
            return null;
        }
    }

    @GetMapping("/productsByRegionId/{regionId}")
    public ResponseEntity<ResponseMessage> productsByRegionId(@PathVariable(name = "regionId") int regionId, @RequestParam(name = "page", defaultValue = "0") int page,
                                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<ProductDto> products = productService.getAllProductsByRegionId(page, size, regionId);
        PaginationData paginationData = new PaginationData(products);
        return ResponseEntity.ok(ResponseMessage
                .builder()
                .data(products.getContent())
                .message("Products retrieved successfully by regionId")
                .status(200)
                .meta(paginationData)
                .build());
    }

    @GetMapping("/productsBySubRegionId/{subRegionId}")
    public ResponseEntity<ResponseMessage> productsBySubRegionId(@PathVariable(name = "subRegionId") int subRegionId, @RequestParam(name = "page", defaultValue = "0") int page,
                                                                 @RequestParam(name = "size", defaultValue = "10") int size) {
        Page<ProductDto> products = productService.getAllProductsBySubRegionId(page, size, subRegionId);
        PaginationData paginationData = new PaginationData(products);
        return ResponseEntity.ok(ResponseMessage
                .builder()
                .data(products.getContent())
                .message("Products retrieved successfully by subRegionId")
                .status(200)
                .meta(paginationData)
                .build());
    }

    @GetMapping("/{productId}/favorites/count")
    public ResponseEntity<ResponseMessage> getProductFavoriteCount(@PathVariable Long productId) {
        long favoriteCount = favoriteService.countFavoritesByProductId(productId);
        return ResponseEntity.ok(ResponseMessage.builder()
                .status(200)
                .message("Favorite count retrieved successfully")
                .data(favoriteCount)
                .build());
    }

    @PostMapping("/{productId}/markPaid")
    public ResponseEntity<ResponseMessage> markProductAsPaid(@PathVariable Long productId) {
        boolean markedAsPaid = productService.markProductAsPaid(productId);
        String message = markedAsPaid ? "Product marked as paid successfully" : "Product removed from paid list";
        return ResponseEntity.ok(ResponseMessage.builder()
                .status(200)
                .message(message)
                .build());
    }


}
