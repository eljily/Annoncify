package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.services.CategoryService;
import com.sibrahim.annoncify.services.SubCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    @PostMapping("/addSubCategory/{categoryId}")
    public ResponseEntity<ResponseMessage> saveSubCategory(@RequestBody SubCategoryDto subCategory,@PathVariable Long categoryId){
        return ResponseEntity.ok(ResponseMessage.builder()
                        .message("Sub Category Added Successfully")
                        .data(subCategoryService.createSubCategory(subCategory,categoryId))
                .build());
    }

    @GetMapping("/SubCategoriesByCategoryId/{categoryId}")
    public ResponseEntity<ResponseMessage> getAllProducts(@RequestParam(name = "page", defaultValue = "0") int page,
                                                          @RequestParam(name = "size", defaultValue = "20") int size,
                                                          @PathVariable(name="categoryId") Long categoryId) {
        return ResponseEntity.ok(ResponseMessage.builder()
                        .message("Retrieved Sub categories By Category ID.")
                        .data(subCategoryService.getByCategoryId(categoryId))
                .build());
    }
}
