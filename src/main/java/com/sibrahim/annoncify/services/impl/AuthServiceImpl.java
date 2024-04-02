package com.sibrahim.annoncify.services.impl;

import com.sibrahim.annoncify.dto.*;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.security.JwtService;
import com.sibrahim.annoncify.services.AuthService;
import com.sibrahim.annoncify.services.OtpService;
import com.sibrahim.annoncify.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final OtpService otpService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    public AuthServiceImpl(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, OtpService otpService, UserMapper userMapper, UserRepository userRepository) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.otpService = otpService;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    @Override
    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        AuthResponseDto authResponseDto = new AuthResponseDto();
        String jwt = "bad request";

        // Authenticate the user using Spring Security's authentication manager
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequestDto.getPhoneNumber(), authRequestDto.getPassword()));

        // If authentication is successful, generate and set the JWT token
        if (authentication.isAuthenticated()) {
            User userDetails = (User) authentication.getPrincipal();
            jwt = jwtService.generateToken(userDetails);
            authResponseDto.setJwt(jwt);
            authResponseDto.setUserId(userDetails.getId());

            // Récupérer le nom de l'utilisateur à partir des détails de l'utilisateur
            String username = userDetails.getName(); // Supposons que le nom d'utilisateur soit stocké dans le champ 'name' de l'objet User
            authResponseDto.setName(username); // Définir le nom de l'utilisateur dans l'objet AuthResponseDto
        }
        return authResponseDto;
    }

    @Override
    public ResponseMessage registerUser(RegisterDto registerDto) {
        // Traitez l'inscription de l'utilisateur ici, par exemple en enregistrant les données dans une base de données
        // Puisque cette partie dépend de votre implémentation spécifique, je vais supposer que l'enregistrement est réussi pour l'exemple

        otpService.sendOtpMessageToUser(registerDto);

        // Créez un objet ResponseMessage pour renvoyer une réponse à l'application frontend
        ResponseMessage response = new ResponseMessage("Inscription réussie");
        // Vous pouvez également ajouter d'autres données pertinentes à renvoyer à l'application frontend si nécessaire

        return response;
    }
    @Override
    public String verify(OtpLoginDto otpLogin) {
        log.info("Phone NUmber"+otpLogin.getPhoneNumber());
        User user = userRepository.findUserByPhoneNumber(otpLogin.getPhoneNumber())
                .orElseThrow(()->new NotFoundException("user not found!"));
        if (otpService.verifyOtp(user, otpLogin.getOtp())){
            return "Account Verified you can now login.";
        }
        return "Invalid Credentials";
    }

    public String resendOtp(String phoneNumber){
        User user = userRepository.findUserByPhoneNumber(phoneNumber)
                .orElseThrow(()->new NotFoundException("User not found !"));
        return otpService.resendOtpMessageToUser(user);
    }
}

