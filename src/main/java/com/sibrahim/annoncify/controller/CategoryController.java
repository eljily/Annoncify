package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.entity.Category;
import com.sibrahim.annoncify.services.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public ResponseEntity<Category> save(@RequestBody Category category){
        return ResponseEntity.ok(categoryService.saveCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> find(@PathVariable Long id){
        try {
            return ResponseEntity.ok(categoryService.getCategory(id).get());
        }catch (Exception e){
            log.error("While Trying to fetch category Message:"+e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        return ResponseEntity.ok(categoryService.deleteCategory(id));
    }
}
