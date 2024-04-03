package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.RoleEnum;
import com.sibrahim.annoncify.services.impl.ImageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserMapper {

    @Autowired
    @Lazy
    private ProductMapper productMapper;

    @Autowired private PasswordEncoder passwordEncoder;

    @Autowired private ImageServiceImpl imageService;

    public UserDto toUserDto(User user){

        return UserDto.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .createDate(user.getCreateDate())
                .updateDate(user.getUpdateDate())
                .products(productMapper.toProductDtos(user.getProducts()))
                .build();
    }

    public User toUser(UserDto userDto){
        return User.builder()
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
                .createDate(userDto.getCreateDate())
                .updateDate(userDto.getUpdateDate())
                .products(productMapper.toProducts(userDto.getProducts()))
                .build();
    }

    public User toUser(RegisterDto userDto, User existingUser) throws IOException {
        User.UserBuilder userBuilder = User.builder();

        if (userDto.getName() != null) {
            userBuilder.name(userDto.getName());
        }

        if (userDto.getFirstName() != null) {
            userBuilder.firstName(userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            userBuilder.lastName(userDto.getLastName());
        }
        if (userDto.getAddress() != null) {
            userBuilder.address(userDto.getAddress());
        }
        if (userDto.getEmail() != null) {
            userBuilder.email(userDto.getEmail());
        }
        if (userDto.getBirthDate() != null) {
            userBuilder.birthDate(userDto.getBirthDate());
        }
        if (userDto.getPhoneNumber() != null) {
            userBuilder.phoneNumber(userDto.getPhoneNumber());
        }

        userBuilder.role(RoleEnum.USER)
                .updateDate(new Date());

        CompletableFuture<String> imageUrlFuture = null;
        if (userDto.getProfilePhoto() != null && !userDto.getProfilePhoto().isEmpty()) {
            imageUrlFuture = uploadProfilePhotoAsync(userDto.getProfilePhoto());
        }
        if (existingUser == null) {
            userBuilder.password(passwordEncoder.encode(userDto.getPassword()))
                    .createDate(new Date());
            if (imageUrlFuture != null) {
                try {
                    userBuilder.profileUrl(imageUrlFuture.get());
                } catch (Exception e) {
                    log.error("Error while trying to upload profile photo");
                }
            }
        } else {
            if (imageUrlFuture != null) {
                try {
                    userBuilder.profileUrl(imageUrlFuture.get());
                } catch (Exception e) {
                    log.error("Error while trying to upload profile photo");
                }
            }
            userBuilder.id(existingUser.getId());
            userBuilder.password(existingUser.getPassword())
                    .createDate(existingUser.getCreateDate());
        }

        return userBuilder.build();
    }

    @Async
    public CompletableFuture<String> uploadProfilePhotoAsync(MultipartFile profilePhoto) {
        return CompletableFuture.completedFuture(imageService.upload(profilePhoto));
    }

    public RegisterDto toDto(User user){
        return RegisterDto.builder()
                .address(user.getAddress())
                .lastName(user.getLastName())
                .name(user.getName())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .id(user.getId())
                .profileUrl(user.getProfileUrl())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .build();
    }

    public List<UserDto> toUserDtos(List<User> users){
        if (users!=null){
            return users
                    .stream()
                    .map(this::toUserDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
