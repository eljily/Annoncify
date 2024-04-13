package com.sibrahim.annoncify.mapper;

import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.dto.UserDto;
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

<<<<<<< Updated upstream
    public User toUser(RegisterDto userDto){
        return User.builder()
                .name(userDto.getName())
                .phoneNumber(userDto.getPhoneNumber())
=======
    /**
     * Converts a RegisterDto object to a User object, optionally updating an existing User.
     * If an existing User is provided, only the fields in the RegisterDto that are not null will be updated.
     * If no existing User is provided, a new User will be created.
     *
     * @param userDto      The RegisterDto object containing user information to be converted.
     * @param existingUser An optional existing User object to be updated.
     * @return A User object created from the RegisterDto and optionally updated with existingUser data.
     * @throws IOException If there's an error during file upload for the profile photo.
     */
    public User toUser(RegisterDto userDto, User existingUser) throws IOException {
        User.UserBuilder userBuilder = User.builder();

        // Set user details from userDto
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

        if (userDto.getPassword() != null) {
            userBuilder.password(passwordEncoder.encode(userDto.getPassword()));
        }

        // Set common fields for both new and existing users
        userBuilder.role(RoleEnum.USER)
                .updateDate(new Date());

        CompletableFuture<String> imageUrlFuture = null;
        // Asynchronously upload profile photo if provided
        if (userDto.getProfilePhoto() != null && !userDto.getProfilePhoto().isEmpty()) {
            imageUrlFuture = uploadProfilePhotoAsync(userDto.getProfilePhoto());
        }

        // If it's a new user, encode password and set creation date
        if (existingUser == null) {
            if (userDto.getPassword()!=null){
                userBuilder.password(passwordEncoder.encode(userDto.getPassword()))
                        .createDate(new Date());
            }

        } else {
            // If it's an existing user, retain existing password and creation date
            userBuilder.id(existingUser.getId());
            userBuilder.password(existingUser.getPassword())
                    .createDate(existingUser.getCreateDate());
        }

        // Set profile URL if profile photo was uploaded
        if (imageUrlFuture != null) {
            try {
                userBuilder.profileUrl(imageUrlFuture.get());
            } catch (Exception e) {
                log.error("Error while trying to upload profile photo");
            }
        }

        return userBuilder.build(); // Build and return the User object
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
>>>>>>> Stashed changes
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
