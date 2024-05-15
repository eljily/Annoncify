package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.repository.CategoryRepository;
import com.sibrahim.annoncify.services.CategoryService;
import jakarta.validation.constraints.Max;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Service
@Slf4j
public class ProductMapper {

    @Autowired
    @Lazy
    private ImageMapper imageMapper;

    @Autowired
    @Lazy
    private UserMapper userMapper;

    @Autowired
    @Lazy
    private CategoryMapper categoryMapper;

    @Autowired private CategoryRepository categoryRepository;

    public ProductDto toProductDto(Product product){
        String subCategoryName = (product.getSubCategory() != null && product.getSubCategory().getName() != null)
                ? product.getSubCategory().getName()
                : "No subcategory";

        return ProductDto.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .createDate(product.getCreateDate())
                .updateDate(product.getUpdateDate())
                .mark(product.getMark())
                .hit(product.getHit())
                .userId(product.getUser() != null ? (product.getUser().getId() != null ? product.getUser().getId() : -1) : -1)
                .subCategory(subCategoryName)
                .vendorDetails(VendorDetails
                        .builder()
                        .phoneNumber(product.getUser()!=null?product.getUser().getPhoneNumber():null)
                        .name(product.getUser()!=null?product.getUser().getName():null)
                        .profileUrl(product.getUser()!=null?product.getUser().getProfileUrl():null)
                        .id(product.getUser()!=null?product.getUser().getId():null)
                        .build())
                .images(imageMapper.toImageDtos(product.getImages()))
                .region(product.getSubRegion()!=null?product.getSubRegion().getRegion().getName():"noo!")
                .subRegion(product.getSubRegion()!=null?product.getSubRegion().getName():"noop!")
                .build();
    }


    public Product toProduct(ProductDto productDto){
        return Product.builder()
                .id(productDto.getId())
                .name(productDto.getName())
                .price(productDto.getPrice())
                .description(productDto.getDescription())
                .images(imageMapper.toImages(productDto.getImages()))
                .createDate(productDto.getCreateDate())
                .updateDate(productDto.getUpdateDate())
                .mark(productDto.getMark())
//                .category(categoryMapper.toCategory(productDto.getCategory()))
                .build();
    }

    public List<ProductDto> toProductDtos(List<Product> products){
        if(products!=null){
            return products
                    .stream()
                    .map(this::toProductDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public List<Product> toProducts(List<ProductDto> productDtos){
        if (productDtos!=null){
            return productDtos
                    .stream()
                    .map(this::toProduct)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    public ProductCardInfoDto toCardInfoDto(Product product){
        ProductCardInfoDto productCardInfoDto = new ProductCardInfoDto();
        productCardInfoDto.setId(product.getId());
        productCardInfoDto.setName(product.getName());
        productCardInfoDto.setPrice(product.getPrice());
        productCardInfoDto.setImages(imageMapper.toImageDtos(product.getImages()))
                ;
        productCardInfoDto.setRegion(product.getSubRegion()!=null?product.getSubRegion().getRegion().getName():"nooooo");
        productCardInfoDto.setSubRegion(product.getSubRegion()!=null?product.getSubRegion().getName():"noooooop");
        return productCardInfoDto;
    }


}
