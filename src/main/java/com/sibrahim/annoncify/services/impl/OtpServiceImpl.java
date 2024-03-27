package com.sibrahim.annoncify.services.impl;


import com.sibrahim.annoncify.config.twilio.TwilioService;
import com.sibrahim.annoncify.dto.RegisterDto;
import com.sibrahim.annoncify.entity.Otp;
import com.sibrahim.annoncify.entity.User;
import com.sibrahim.annoncify.entity.enums.OtpStatus;
import com.sibrahim.annoncify.exceptions.NotFoundException;
import com.sibrahim.annoncify.exceptions.UserAlreadyExist;
import com.sibrahim.annoncify.mapper.UserMapper;
import com.sibrahim.annoncify.repository.OtpRepository;
import com.sibrahim.annoncify.repository.UserRepository;
import com.sibrahim.annoncify.services.OtpService;
import com.sibrahim.annoncify.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final TwilioService twilioService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    @Override
    public Otp save(Otp otp) {
        return otpRepository.save(otp);
    }


    /**
     * Sends an OTP (One Time Password) message to the user via SMS.
     *
     * @param userDto the user DTO containing the information of the user
     * @return a message indicating the status of the OTP sending process
     */
    @Override
    public String sendOtpMessageToUser(RegisterDto userDto) {
        Optional<User> userOptional = userRepository
                .findUserByPhoneNumber(userDto.getPhoneNumber());
        if(userOptional.isPresent()){
            throw new UserAlreadyExist("User already exist with phone number :"+userDto.getPhoneNumber());
        }
        User user = userService.saveUser(userDto);
        Otp otp = buildOtpForUser(user);
        if (isOTPAlreadySentWithinLast30Seconds(user.getId())) {
            return "Otp can be send in after 30 seconds";
        }
        updateOtpIdIfPresent(user.getId(), otp);
        return sendMessageAndSaveStatus(user.getPhoneNumber(), otp);
    }

    /**
     * Verifies the OTP (One Time Password) entered by the user.
     *
     * @param user the user object representing the user
     * @param code the OTP code entered by the user
     * @throws NotFoundException if the OTP is invalid or expired
     */
    @Override
    public boolean verifyOtp(User user, String code) {
        Optional<Otp> otpOptional = otpRepository.findByUserId(user.getId());
        if (otpOptional.isEmpty() || otpOptional.get().getExpiryDatetime().isBefore(LocalDateTime.now())) {
            throw new NotFoundException("Otp is expired");
        }
        Otp otp = otpOptional.get();
        if (!otp.getCode().equals(code)) {
            throw new NotFoundException("Invalid otp");
        }
        otp.setStatus(OtpStatus.VERIFIED);
        otp.setUpdatedDatetime(LocalDateTime.now());
        user.setEnabled(true);
        userRepository.save(user);
        otpRepository.save(otp);
        return true;
    }

    private Otp buildOtpForUser(User user) {
        return Otp.builder()
                .user(user)
                .createdDatetime(LocalDateTime.now())
                .updatedDatetime(LocalDateTime.now())
                .code(generateOtpCode())
                .status(OtpStatus.PENDING)
                .expiryDatetime(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    /**
     * Sends a verification message to the specified mobile number and updates the status of the OTP.
     *
     * @param mobile the mobile number to send the message to
     * @param otp    the OTP object containing the code and status
     * @return a message indicating the status of the OTP sending process
     */
    private String sendMessageAndSaveStatus(String mobile, Otp otp) {
        String message;
        if (twilioService.sendVerificationMessage(mobile, otp.getCode())) {
            otp.setStatus(OtpStatus.DELIVERED);
            message = "OTP has been successfully generated, and awaits your verification";
            log.info("otp sent successfully to :"+mobile);
            //valid format :+22236537673
        } else {
            otp.setStatus(OtpStatus.FAILED);
            log.info("otp failed to be sent to :"+mobile);
            message = "An error occurred while sending the OTP.";
        }
        save(otp);
        return message;
    }

    /**
     * Checks if an OTP (One Time Password) has already been sent to a user within the last 30 seconds.
     *
     * @param userId the ID of the user
     * @return {@code true} if an OTP has been sent within the last 30 seconds, {@code false} otherwise
     */
    private boolean isOTPAlreadySentWithinLast30Seconds(Long userId) {
        Optional<Otp> otpOptional = otpRepository.findByUserId(userId);
        return otpOptional.isPresent() && otpOptional.get().getUpdatedDatetime().isAfter(LocalDateTime.now().minusSeconds(30));
    }

    private void updateOtpIdIfPresent(Long userId, Otp otp) {
        Optional<Otp> otpOptional = otpRepository.findByUserId(userId);
        otpOptional.ifPresent(value -> otp.setId(value.getId()));
    }

    /**
     * Generates a random OTP (One Time Password) code.
     *
     * @return the generated OTP code
     */
    private String generateOtpCode() {
        return new DecimalFormat("0000")
                .format(new Random().nextInt(9999));
    }
}

