package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.LoginDto;
import com.sibrahim.annoncify.dto.LoginResponseDto;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.UserService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final JwtService jwtService;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDto saveUser(RegisterDto registerDto) {
        User user = userMapper.toUser(registerDto);
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
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
    public LoginResponseDto login(LoginDto loginDto) {
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        String jwt = "bad request";
        Optional<User> user = userRepository.findUserByPhoneNumber(loginDto.getPhoneNumber());
        if(user.isPresent() && Objects.equals(user.get().getPassword(), passwordEncoder.encode(loginDto.getPassword()))){
            jwt = jwtService.generateToken(user.get());
            loginResponseDto.setJwt(jwt);
            return loginResponseDto;
        }
        loginResponseDto.setJwt(jwt);
        return loginResponseDto;
    }
}
