package com.koomi.tradingplatfrom.controller;

import com.koomi.tradingplatfrom.request.ForgotPassWordTokenRequest;
import com.koomi.tradingplatfrom.Utils.OtpUtils;
import com.koomi.tradingplatfrom.domain.VerificationType;
import com.koomi.tradingplatfrom.exception.OtpValidationException;
import com.koomi.tradingplatfrom.model.entity.ForgotPassWordToken;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.model.entity.VerificationCode;
import com.koomi.tradingplatfrom.response.ApiResponse;
import com.koomi.tradingplatfrom.response.AuthResponse;
import com.koomi.tradingplatfrom.request.ResetPasswordRequest;
import com.koomi.tradingplatfrom.service.EmailService;
import com.koomi.tradingplatfrom.service.ForgotPasswordService;
import com.koomi.tradingplatfrom.service.UserService;
import com.koomi.tradingplatfrom.service.VerificationCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private EmailService emailService;


    @GetMapping("api/user/profile")
    public ResponseEntity<User> getUserProfile( @RequestHeader("Authorization") String jwt) {
        User user = userService.findUserProfileByJwt(jwt);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    @PostMapping("api/user/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt,
                                                    @PathVariable VerificationType verificationType) {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUserId(user.getId());

        if(verificationCode == null){
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
        }
        if(verificationType.equals(VerificationType.EMAIL)){
            emailService.sendVerificationEmail(user.getEmail(), verificationCode.getOpt());
        }

        return new ResponseEntity<>("verification otp sent susscessfully", HttpStatus.OK);
    }

    @PatchMapping("api/user/enable-two-factor-authentication/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(
            @PathVariable String otp,
            @RequestHeader("Authorization") String jwt) {

        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUserId(user.getId());

        String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL) ? user.getEmail() : user.getPhoneNumber();

        boolean isVerified = verificationCode.getOpt().equals(otp);

        if(isVerified){
            User updatedUser = userService.enableTwoFactorAuth(verificationCode.getVerificationType(), sendTo, user);
            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }

        throw new OtpValidationException("Invalid OTP");
    }

    @PatchMapping("auth/user/reset-password/send-otp")
    public ResponseEntity<AuthResponse> SenFogotPassWordOtp(
            @RequestBody ForgotPassWordTokenRequest forgotPassWordTokenRequest) {

        User user = userService.findUserByEmail(forgotPassWordTokenRequest.getSendTo());
        String otp = OtpUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPassWordToken token = forgotPasswordService.findByUser(user.getId());

        if(token == null){
            token = forgotPasswordService.createForgotPasswordToken(user, otp, forgotPassWordTokenRequest.getVerificationType() , forgotPassWordTokenRequest.getSendTo());
        }

        if(forgotPassWordTokenRequest.getVerificationType().equals(VerificationType.EMAIL)){
            emailService.sendVerificationEmail(forgotPassWordTokenRequest.getSendTo(), token.getOtp());
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setSession(token.getId().toString());
        authResponse.setMessage("password reset otp sent susscessfully");

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    @PatchMapping("auth/user/reset-password/verify-otp/")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam("otp") String id,
            @RequestBody ResetPasswordRequest resetPasswordRequest) {


        ForgotPassWordToken token = forgotPasswordService.findById(Long.parseLong(id));

        boolean isVerified = token.getOtp().equals(resetPasswordRequest.getOtp());

        if(isVerified){
            userService.updatePassword(token.getUser(), resetPasswordRequest.getPassword());
            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setMessage("password update susscessfully");
        }

        return new ResponseEntity<>(new ApiResponse(), HttpStatus.ACCEPTED);
    }

}
