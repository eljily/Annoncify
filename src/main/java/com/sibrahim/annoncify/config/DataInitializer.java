package com.sibrahim.annoncify.config;

import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.entity.SubCategory;
import com.sibrahim.annoncify.repository.CategoryRepository;
import com.sibrahim.annoncify.repository.SubCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

//@Configuration
public class DataInitializer {


    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public DataInitializer(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    //@Bean
    public CommandLineRunner initializeData() {
        return args -> {
            initializeCategories();
            initializeSubCategories();
        };
    }

    private void initializeCategories() {
        List<String> categoryNames = List.of("Real Estate", "Vehicles", "Electronics", "Fashion & Accessories", "Other");
        for (String categoryName : categoryNames) {
            Optional<Category> existingCategory = categoryRepository.findCategoryByName(categoryName);
            if (existingCategory.isEmpty()) {
                categoryRepository.save(Category.builder()
                                .createDate(LocalDateTime.now())
                                .name(categoryName)
                        .build());
            }
        }
    }

    private void initializeSubCategories() {
        // Real Estate Subcategories
        initializeSubCategory("Apartments & Flats", "Real Estate");
        initializeSubCategory("Houses & Villas", "Real Estate");
        initializeSubCategory("Commercial Spaces", "Real Estate");
        initializeSubCategory("Land & Plots", "Real Estate");

        // Vehicles Subcategories
        initializeSubCategory("Cars", "Vehicles");
        initializeSubCategory("Motorcycles", "Vehicles");
        initializeSubCategory("Trucks & Commercial Vehicles", "Vehicles");
        initializeSubCategory("Boats & Watercraft", "Vehicles");

        // Electronics Subcategories
        initializeSubCategory("Mobile Phones & Tablets", "Electronics");
        initializeSubCategory("Computers & Laptops", "Electronics");
        initializeSubCategory("TVs & Home Theater Systems", "Electronics");
        initializeSubCategory("Cameras & Photography Equipment", "Electronics");

        // Fashion & Accessories Subcategories
        initializeSubCategory("Clothing", "Fashion & Accessories");
        initializeSubCategory("Shoes", "Fashion & Accessories");
        initializeSubCategory("Bags & Luggage", "Fashion & Accessories");
        initializeSubCategory("Watches & Jewelry", "Fashion & Accessories");

        // Other Subcategory
        initializeSubCategory("Other", "Other");
    }

    private void initializeSubCategory(String subCategoryName, String categoryName) {
        Optional<Category> category = categoryRepository.findCategoryByName(categoryName);
        if (category.isPresent()) {
            Optional<SubCategory> existingSubCategory = subCategoryRepository.findByCategoryAndName(category.get(), subCategoryName);
            if (existingSubCategory.isEmpty()) {
                subCategoryRepository.save(SubCategory.builder()
                                .name(subCategoryName)
                                .category(category.get())
                                .createDate(LocalDateTime.now())
                        .build());
            }
        }
    }
}
