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

@Configuration
public class DataInitializer {

    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;

    public DataInitializer(CategoryRepository categoryRepository, SubCategoryRepository subCategoryRepository) {
        this.categoryRepository = categoryRepository;
        this.subCategoryRepository = subCategoryRepository;
    }

    @Bean
    public CommandLineRunner initializeData() {
        return args -> {
            initializeCategories();
            initializeSubCategories();
        };
    }

    private void initializeCategories() {
        List<CategoryData> categories = List.of(
                new CategoryData("Vehicles", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/c0195254-f7fc-4476-b8d9-bebb2355d031.png?alt=media"),
                new CategoryData("Real Estate", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/c4eff967-e65f-4eaf-9a32-a754be6ed661.png?alt=media"),
                new CategoryData("Phones", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/949c6efe-590a-4bb1-8024-635715a5ef55.png?alt=media"),
                new CategoryData("Electronics", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/138bc0ef-7ed7-49b0-af7c-d4c416d659b8.png?alt=media"),
                new CategoryData("Home Appliances", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/f6b29cac-006c-4a17-8a41-ae0ba421cc82.png?alt=media"),
                new CategoryData("Jobs & Services", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/914a26cc-bdba-49ef-8b16-fb572d01f8ea.png?alt=media"),
                new CategoryData("Materials & Equipment", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/aa89de84-89af-4345-b196-7b71b8ff6464.png?alt=media"),
                new CategoryData("Fashion & Beauty", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/99f8c212-ae95-4f39-ac69-225401ff9c5a.png?alt=media"),
                new CategoryData("Leisure & Games", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/53c520dc-be82-4bff-9a70-76ebb522049a.png?alt=media"),
                new CategoryData("Other", "https://firebasestorage.googleapis.com/v0/b/annoncify-front.appspot.com/o/71ca30b4-02e6-489d-90e5-2146e54798a5.png?alt=media")
        );

        for (CategoryData categoryData : categories) {
            Optional<Category> existingCategory = categoryRepository.findCategoryByName(categoryData.name);
            if (existingCategory.isEmpty()) {
                categoryRepository.save(Category.builder()
                        .createDate(LocalDateTime.now())
                        .name(categoryData.name)
                        .imgUrl(categoryData.imageUrl)
                        .build());
            }
        }
    }

    private void initializeSubCategories() {
        // Vehicles Subcategories
        initializeSubCategory("Cars", "Vehicles");
        initializeSubCategory("Motorcycles", "Vehicles");
        initializeSubCategory("Bicycles", "Vehicles");
        initializeSubCategory("Parts & Accessories", "Vehicles");
        initializeSubCategory("Other", "Vehicles");

        // Real Estate Subcategories
        initializeSubCategory("Apartments", "Real Estate");
        initializeSubCategory("Houses & Villas", "Real Estate");
        initializeSubCategory("Land", "Real Estate");
        initializeSubCategory("Other", "Real Estate");

        // Phones Subcategories
        initializeSubCategory("New", "Phones");
        initializeSubCategory("Used", "Phones");
        initializeSubCategory("Accessories", "Phones");
        initializeSubCategory("Other", "Phones");

        // Electronics Subcategories
        initializeSubCategory("Computers", "Electronics");
        initializeSubCategory("Televisions", "Electronics");
        initializeSubCategory("Consoles", "Electronics");
        initializeSubCategory("Video Games", "Electronics");
        initializeSubCategory("Accessories", "Electronics");
        initializeSubCategory("Other", "Electronics");

        // Home Appliances Subcategories
        initializeSubCategory("Dishwashers", "Home Appliances");
        initializeSubCategory("Refrigerators", "Home Appliances");
        initializeSubCategory("Washing Machines", "Home Appliances");
        initializeSubCategory("Freezers", "Home Appliances");
        initializeSubCategory("Ovens", "Home Appliances");
        initializeSubCategory("Other", "Home Appliances");

        // Jobs & Services Subcategories
        initializeSubCategory("Job Offers", "Jobs & Services");
        initializeSubCategory("Job Requests", "Jobs & Services");
        initializeSubCategory("Service", "Jobs & Services");
        initializeSubCategory("Other", "Jobs & Services");

        // Materials & Equipment Subcategories
        initializeSubCategory("Industry", "Materials & Equipment");
        initializeSubCategory("Electricity & Energy", "Materials & Equipment");
        initializeSubCategory("Plumbing", "Materials & Equipment");
        initializeSubCategory("Other", "Materials & Equipment");

        // Fashion & Beauty Subcategories
        initializeSubCategory("Clothing", "Fashion & Beauty");
        initializeSubCategory("Shoes", "Fashion & Beauty");
        initializeSubCategory("Accessories", "Fashion & Beauty");
        initializeSubCategory("Beauty Products", "Fashion & Beauty");
        initializeSubCategory("Other", "Fashion & Beauty");

        // Leisure & Games Subcategories
        initializeSubCategory("Sports & Leisure", "Leisure & Games");
        initializeSubCategory("Games & Toys", "Leisure & Games");
        initializeSubCategory("Travel", "Leisure & Games");
        initializeSubCategory("Other", "Leisure & Games");

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

    private static class CategoryData {
        String name;
        String imageUrl;

        CategoryData(String name, String imageUrl) {
            this.name = name;
            this.imageUrl = imageUrl;
        }
    }

}
