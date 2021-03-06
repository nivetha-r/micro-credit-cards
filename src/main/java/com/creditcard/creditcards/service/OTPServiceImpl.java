package com.creditcard.creditcards.service;

import com.creditcard.creditcards.dto.ApiResponse;
import com.creditcard.creditcards.dto.OTPResponseDto;
import com.creditcard.creditcards.entity.OTP;
import com.creditcard.creditcards.entity.User;
import com.creditcard.creditcards.exception.CustomException;
import com.creditcard.creditcards.repository.OTPRepository;
import com.creditcard.creditcards.repository.UserRepository;
import com.creditcard.creditcards.util.Constants;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * The Otp service.
 */
@Service
public class OTPServiceImpl implements OTPService {

    private Logger logger = LoggerFactory.getLogger(OTPServiceImpl.class);


    @Autowired
    private OTPRepository otpRepository;


    @Autowired
    private UserRepository userRepository;

    @Override
    public OTPResponseDto getOtp(Long userId) {
        logger.info("Getting OTP service ");
        OTPResponseDto otpResponseDto = new OTPResponseDto();
        OTP otp = otpRepository.findByUserUserId(userId);
        otpResponseDto.setOtpValue(otp.getOtpValue());
        otpResponseDto.setDate(otp.getDate());
        logger.info(" OTP values :" + otpResponseDto);
        return otpResponseDto;
    }

    @Override
    public ApiResponse generateOtp(Long userId) {
        ApiResponse response = new ApiResponse();
        logger.info("Generating OTP service ");
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            OTP otp = new OTP();
            otp.setDate(LocalDate.now());
            User user = userOptional.get();
            otp.setOtpValue(Integer.parseInt(Constants.generateRandomNumbers(StringUtils.EMPTY, 5)));
            otp.setUser(user);
            otpRepository.save(otp);
            logger.info("Saving OTP service ");
            response.setStatusCode(Constants.SUCCESS_CODE);
        } else {
            throw new CustomException("User not found");
        }
        return response;
    }
}
