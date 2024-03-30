package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.RoleEnum;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.ProductService;
import com.sibrahim.annoncify.services.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           ProductService productService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
    }

    @Override
    public User saveUser(RegisterDto registerDto) {
        User user = userMapper.toUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setEmail(registerDto.getEmail());
        user.setRole(RoleEnum.USER);
        user.setCreateDate(new Date());
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    public Page<UserDto> getAllUsers(int page,int size) {
        Pageable pageable = PageRequest.of(page,size);
        return userRepository.findAll(pageable).map(userMapper::toUserDto);
    }

    @Override
    public Optional<UserDto> getUserByPhoneNumber(String phoneNumber) {
        return Optional.empty();
    }

    @Override
    public ProductDto addProduct(ProductDto productDto) {
        return productService.saveProduct(productDto);
    }

    @Override
    public void deleteProduct(Long id) {
        Optional<Product> product = productService.getById(id);
        if (product.isPresent()){
            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            // Extract user details from Authentication
            User user = (User) authentication.getPrincipal();
            if(Objects.equals(user.getId(), product.get().getUser().getId())){
                productService.deleteProduct(id);
            }
        }
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return userMapper.toUserDto(user);
    }

    @Override
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<ProductDto> getProducts() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        // Extract user details from Authentication
        User user = (User) authentication.getPrincipal();
        return productService.getProductsByUserId(user.getId());
    }

}
