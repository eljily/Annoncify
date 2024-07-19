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

    public UserDto toUserDto(User user){

        return UserDto.builder()
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .createDate(user.getCreateDate())
                .updateDate(user.getUpdateDate())
                //.products(productMapper.toProductDtos(user.getProducts()))
                .role(user.getRole().name())
                .build();
    }

    public User toUser(UserDto userDto){
        return User.builder()
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
                .createDate(userDto.getCreateDate())
                .updateDate(userDto.getUpdateDate())
                //.products(productMapper.toProducts(userDto.getProducts()))
                .build();
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
                .whatsAppNumber(user.getWhatsAppNumber())
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
