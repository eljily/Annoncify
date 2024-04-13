package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.RoleEnum;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.services.ImageService;
import com.sibrahim.annoncify.services.ProductService;
import com.sibrahim.annoncify.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ProductService productService;
    private final ImageService imageService;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           PasswordEncoder passwordEncoder,
                           ProductService productService, ImageService imageService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.productService = productService;
        this.imageService = imageService;
    }

    @Override
    public RegisterDto saveUser(RegisterDto registerDto,Long id) throws IOException {
        User existingUser = null;
        if (id!=null){
            existingUser = userRepository
                    .findById(id)
                    .orElseThrow(()->new NotFoundException("user not found with id :"+id));
        }
        if (registerDto.getPhoneNumber() != null) {
            existingUser = userRepository
                    .findUserByPhoneNumber(registerDto.getPhoneNumber())
                    .orElse(null);
        }

        User.UserBuilder userBuilder = User.builder()
                .name(registerDto.getName())
                .firstName(registerDto.getFirstName())
                .lastName(registerDto.getLastName())
                .address(registerDto.getAddress())
                .email(registerDto.getEmail())
                .birthDate(registerDto.getBirthDate())
                .phoneNumber(registerDto.getPhoneNumber())
                .role(RoleEnum.USER)
                .updateDate(new Date());

        CompletableFuture<String> imageUrlFuture = null;
        if (registerDto.getProfilePhoto() != null && !registerDto.getProfilePhoto().isEmpty()) {
            imageUrlFuture = uploadProfilePhotoAsync(registerDto.getProfilePhoto());
        }

        if (existingUser == null) {
            userBuilder.password(passwordEncoder.encode(registerDto.getPassword()))
                    .createDate(new Date());
            userBuilder.isEnabled(true);
        } else {
            userBuilder.password(existingUser.getPassword())
                    .createDate(existingUser.getCreateDate())
                    .id(existingUser.getId());
        }

        if (imageUrlFuture != null) {
            try {
                userBuilder.profileUrl(imageUrlFuture.get());
            } catch (Exception e) {
                log.error("Error while trying to upload profile photo");
            }
        }

        User user = userBuilder.build();
        user = userRepository.save(user);

        return userMapper.toDto(user);
    }

    @Async
    public CompletableFuture<String> uploadProfilePhotoAsync(MultipartFile profilePhoto) {
        return CompletableFuture.completedFuture(imageService.upload(profilePhoto));
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

//    @Override
//    public ProductDto addProduct(ProductDto productDto) {
//        return productService.saveProduct(productDto);
//    }

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
    public RegisterDto getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow();
        return userMapper.toDto(user);
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
