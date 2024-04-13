package com.sibrahim.annoncify.controller;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ResponseMessage> getUsers(@RequestParam(name = "page", defaultValue = "0") int page,
                                                    @RequestParam(name = "size", defaultValue = "2") int size) {
        Page<UserDto> usersPage = userService.getAllUsers(page, size);
        PaginationData paginationData = new PaginationData(usersPage);
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("users list fetched successfully ")
                .status(HttpStatus.OK.value())
                .data(usersPage.getContent())
                .meta(paginationData)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMessage> getUser(@PathVariable Long id){
            return ResponseEntity.ok(ResponseMessage.builder()
                    .status(200)
                    .message("USer Retrieved successfully")
                    .data(userService.getUserById(id))
                    .build());
    }

    @GetMapping("/myProducts")
    public ResponseEntity<List<ProductDto>> getProducts(){
        try {
            return ResponseEntity.ok(userService.getProducts());
        }catch (Exception e){
            log.error("ERROR WHILE FETCHING USER PRODUCTS ,message:"+e.getMessage());
            return null;
        }

    }

//    @PostMapping("/addProduct")
//    public ResponseEntity<ProductDto> saveProduct(@RequestBody ProductDto productDto){
//        try {
//            return ResponseEntity.ok(userService.addProduct(productDto));
//        }catch (Exception e){
//           log.error("ERROR WHILE ADDING NEW PRODUCT BY USER,message:"+e.getMessage());
//            return null;
//        }
//    }

    @DeleteMapping("/deleteProduct/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        try {
            userService.deleteProduct(id);
            return ResponseEntity.ok(HttpStatus.valueOf(200));
        }catch (Exception e){
            log.error("ERROR WHILE DELETING PRODUCT BY ID,message:"+e.getMessage());
            return null;
        }
    }

    @PostMapping
    public ResponseEntity<ResponseMessage> addUser(@ModelAttribute RegisterDto registerDto) throws IOException {
        return ResponseEntity.ok(ResponseMessage.builder()
                        .message("User added successfully ")
                        .status(HttpStatus.OK.value())
                        .data(userService.saveUser(registerDto,null))
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseMessage> updateUser(@ModelAttribute RegisterDto registerDto,
                                                      @PathVariable(name = "id") Long id) throws IOException {
        return ResponseEntity.ok(ResponseMessage.builder()
                .message("User updated successfully ")
                .status(HttpStatus.OK.value())
                .data(userService.saveUser(registerDto,id))
                .build());
    }
}
