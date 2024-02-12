package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.LoginDto;
import com.sibrahim.annoncify.dto.ProductDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
import com.sibrahim.annoncify.entity.Product;
import com.sibrahim.annoncify.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
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

    public User toUser(RegisterDto userDto){
        return User.builder()
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
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
