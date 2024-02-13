package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.RoleEnum;
import com.sibrahim.annoncify.mapper.ProductMapper;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.ProductService;
import com.sibrahim.annoncify.services.UserService;
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
    private final ProductMapper productMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           ProductMapper productMapper,
                           PasswordEncoder passwordEncoder,
                           ProductService productService, JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.productMapper = productMapper;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public UserDto saveUser(RegisterDto registerDto) {
        User user = userMapper.toUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRole(RoleEnum.USER);
        user.setCreateDate(new Date());
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toUserDtos(userRepository.findAll());
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
    public LoginResponseDto login(LoginDto loginDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String jwt = "bad request";
        // Authenticate the user using Spring Security's authentication manager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getPhoneNumber(), loginDto.getPassword()));

        // If authentication is successful, generate and set the JWT token
        if (authentication.isAuthenticated()) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            jwt = jwtService.generateToken(userDetails);
            loginResponseDto.setJwt(jwt);
        }
        return loginResponseDto;
    }
}
