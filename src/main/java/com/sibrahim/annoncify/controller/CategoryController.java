package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.CategoryDto;
import com.sibrahim.annoncify.dto.ResponseMessage;
import com.sibrahim.annoncify.dto.SubCategoryDto;
import com.sibrahim.annoncify.services.CategoryService;
import com.sibrahim.annoncify.services.SubCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    private final SubCategoryService subCategoryService;

    public CategoryController(CategoryService categoryService, SubCategoryService subCategoryService) {
        this.categoryService = categoryService;
        this.subCategoryService = subCategoryService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> save(@RequestBody CategoryDto category){
        return ResponseEntity.ok(categoryService.saveCategory(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll(){
        return ResponseEntity.ok(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> find(@PathVariable Long id){
        try {
            return ResponseEntity.ok(categoryService.getCategory(id));
        }catch (Exception e){
            log.error("While Trying to fetch category Message:"+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }

    @PostMapping("/addSubCategory")
    public ResponseEntity<ResponseMessage> saveSubCategory(@RequestBody SubCategoryDto subCategory){
        return ResponseEntity.ok(ResponseMessage.builder()
                        .message("Sub Category Added Successfully")
                        .data(subCategoryService.createSubCategory(subCategory))
                .build());
    }
}
