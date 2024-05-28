package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.dto.CategoryResponseDto;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.SubCategoryDto;
import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.SubCategory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryMapper {



  /*  public Category toCategory(CategoryDto categoryDto){
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        category.setProductList(productMapper.toProducts(categoryDto.getProducts()));
        return category;
    }*/

//    public CategoryDto toCategoryDto(Category category){
//        CategoryDto categoryDto = new CategoryDto();
//        categoryDto.setId(category.getId());
//        categoryDto.setName(category.getName());
//        categoryDto.setProducts(productMapper.toProductDtos(category.getProductList()));
//        return categoryDto;
//    }

    public List<CategoryDto> toCategoryDtos(List<Category> categories){
        if(categories!=null){
            return categories
                    .stream()
                    .map(this::toCategoryDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<SubCategoryDto> toSubCategoryDtos(List<SubCategory> categories){
        if(categories!=null){
            return categories
                    .stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<Category> toCategories(List<CategoryDto> categories){
        if(categories!=null){
            return categories
                    .stream()
                    .map(this::toCategory)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public Category toCategory(CategoryDto categoryDto) {
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        //category.setProductList(mapBasicProducts(categoryDto.getProducts()));
        return category;
    }

    public CategoryDto toCategoryDto(Category category) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());
        categoryDto.setSubCategories(mapBasicSubCategoryDtos(category.getSubCategories()));
        categoryDto.setImageUrl(category.getImgUrl());
        return categoryDto;
    }

    private List<Product> mapBasicProducts(List<ProductDto> productDtos) {
        if (productDtos != null) {
            return productDtos.stream()
                    .map(this::mapBasicProduct)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private List<SubCategoryDto> mapBasicSubCategoryDtos(List<SubCategory> subCategories) {
        if (subCategories != null) {
            return subCategories.stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    private Product mapBasicProduct(ProductDto productDto) {
        return (productDto != null) ? Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .createDate(productDto.getCreateDate())
                .updateDate(productDto.getUpdateDate())
                .build() : null;
    }

    private ProductDto mapBasicProductDto(Product product) {
        return (product != null) ? ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .createDate(product.getCreateDate())
                .updateDate(product.getUpdateDate())
                .build() : null;
    }

    private List<ProductDto> mapBasicProductsDto(List<Product> products) {
        if (products != null) {
            return products.stream()
                    .map(this::mapBasicProductDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public CategoryResponseDto toCategoryResponseDto(Category category){
        if (category!=null){
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(category.getId());
            categoryResponseDto.setName(category.getName());
            return categoryResponseDto;
        }
       return new CategoryResponseDto();
    }

    public CategoryResponseDto toCategoryResponseDto(CategoryDto category){
        if (category!=null){
            CategoryResponseDto categoryResponseDto = new CategoryResponseDto();
            categoryResponseDto.setId(category.getId());
            categoryResponseDto.setName(category.getName());
            return categoryResponseDto;
        }
        return new CategoryResponseDto();
    }

    public Category toCategory(CategoryResponseDto categoryResponseDto){
        if (categoryResponseDto!=null){
            Category category = new Category();
            category.setId(categoryResponseDto.getId());
            category.setName(categoryResponseDto.getName());
            return category;
        }
        return new Category();
    }

    public SubCategory toModel(SubCategoryDto subCategoryDto){
        return SubCategory.builder()
                .id(subCategoryDto.getId())
                .name(subCategoryDto.getName())
                .build();
    }

    public SubCategoryDto toDTO(SubCategory subCategory){
        return SubCategoryDto.builder()
                .id(subCategory.getId())
                .name(subCategory.getName())
                .build();
    }

}
