package com.sibrahim.annoncify.services;

import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.ProductRequestDto;
import com.sibrahim.annoncify.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface ProductService {

    Page<ProductDto> getAllProducts(int page,int size);

    Page<ProductDto> getAllProducts(int page,int size,int categoryId);

    Page<ProductDto> getAllProductsByCategoryId(int page,int size,int categoryId);

    Page<ProductDto> getProductsByKeyword(int page,int size,String keyword);

    List<ProductDto> getProductsByUserId(Long id);

    Optional<ProductDto> getProductById(Long id);

    Optional<Product> getById(Long id);

    void deleteProduct(Long id);

    //deprecated
//    ProductDto saveProduct(ProductDto product);

//    Product addProductWithImages(Product product, List<MultipartFile> imageFiles);

//    String uploadImageToFirebase(MultipartFile imageFile);

    ProductDto addProduct(ProductRequestDto productRequestDto);

    List<Product> findLastEightProductsByCategoryId(Long id);

    Page<ProductDto> getAllProductsByRegionId(int page,int size,int regionId);

    Page<ProductDto> getAllProductsBySubRegionId(int page,int size,int subRegionId);

}
