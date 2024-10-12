package com.koomi.treadingplatfrom.controller;

import com.koomi.treadingplatfrom.Utils.OtpUtils;
import com.koomi.treadingplatfrom.config.JwtProvider;
import com.koomi.treadingplatfrom.domain.ContactMethod;
import com.koomi.treadingplatfrom.exception.EmailAlreadyExistsException;
import com.koomi.treadingplatfrom.exception.InvalidCredentialsException;
import com.koomi.treadingplatfrom.exception.OtpValidationException;
import com.koomi.treadingplatfrom.exception.UserNotFoundException;
import com.koomi.treadingplatfrom.model.entity.TwoFactorOTP;
import com.koomi.treadingplatfrom.model.entity.User;
import com.koomi.treadingplatfrom.repository.UserRepository;
import com.koomi.treadingplatfrom.response.AuthResponse;
import com.koomi.treadingplatfrom.service.TwoFactorOtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuController {

    private static final Logger logger = LoggerFactory.getLogger(AuController.class);

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;
    private final TwoFactorOtpService twoFactorOtpService;
    private final AuthenticationManager authenticationManager;

    public AuController(UserRepository userRepository, JwtProvider jwtProvider,
                        PasswordEncoder passwordEncoder, TwoFactorOtpService twoFactorOtpService,
                        AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtProvider = jwtProvider;
        this.passwordEncoder = passwordEncoder;
        this.twoFactorOtpService = twoFactorOtpService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        logger.info("Registering new user: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()) != null) {
            logger.warn("Registration failed: Email already exists - {}", user.getEmail());
            throw new EmailAlreadyExistsException("Email already exists");
        }

        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(newUser);
        logger.info("User registered successfully: {}", savedUser.getEmail());

        Authentication auth = new UsernamePasswordAuthenticationToken(newUser.getEmail(), null, List.of());
        String jwt = jwtProvider.generateToken(auth);

        AuthResponse res = new AuthResponse();
        res.setJwt(jwt);
        res.setStatus(true);
        res.setMessage("User registered successfully");
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) {
        logger.info("Login attempt for user: {}", user.getEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser == null) {
                logger.warn("Login failed: User not found - {}", user.getEmail());
                throw new UserNotFoundException("User not found");
            }

            if (existingUser.getTwoFactorAuth().isEnable()) {
                logger.info("Two-factor authentication required for user: {}", user.getEmail());
                return handleTwoFactorAuth(existingUser);
            }

            String jwt = jwtProvider.generateToken(authentication);
            logger.info("User logged in successfully: {}", user.getEmail());
            AuthResponse res = new AuthResponse();
            res.setJwt(jwt);
            res.setStatus(true);
            res.setMessage("User logged in successfully");
            return ResponseEntity.ok(res);

        } catch (Exception e) {
            logger.error("Login failed for user: {}", user.getEmail(), e);
            throw new InvalidCredentialsException("Invalid email or password");
        }
    }


    private ResponseEntity<AuthResponse> handleTwoFactorAuth(User user) {
        String otp = OtpUtils.generateOTP();
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findByUser(user.getId());

        if (twoFactorOTP == null) {
            // If no OTP exists for this user, create a new one
            twoFactorOTP = new TwoFactorOTP();
            twoFactorOTP.setUser(user);
        }

        // Update OTP details
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setExpiryTime(LocalDateTime.now().plusMinutes(5)); // OTP expires in 5 minutes
        twoFactorOtpService.save(twoFactorOTP);

        // Send OTP to user
//        try {
//            sendOtpToUser(user, otp);
//        } catch (Exception e) {
//            logger.error("Failed to send OTP to user: {}", user.getEmail(), e);
//            throw new RuntimeException("Failed to send OTP. Please try again.");
//        }

        AuthResponse res = new AuthResponse();
        res.setMessage("Two-factor authentication is required. An OTP has been sent to your registered email/phone.");
        res.setTwoFactorAuthEnabled(true);
        res.setRequireOtp(true);

        // You might want to include a temporary token here that the client can use
        // when submitting the OTP for verification
        String tempToken = jwtProvider.generateTempToken(user.getEmail());
        res.setTempToken(tempToken);

        return ResponseEntity.ok(res);
    }

//    private void sendOtpToUser(User user, String otp) {
//        // Implement the logic to send OTP to user's email or phone
//        // This is just a placeholder implementation
//        if (user.getPreferredContactMethod() == ContactMethod.EMAIL) {
//            emailService.sendOtp(user.getEmail(), otp);
//        } else if (user.getPreferredContactMethod() == ContactMethod.SMS) {
//            smsService.sendOtp(user.getPhoneNumber(), otp);
//        } else {
//            throw new IllegalStateException("No valid contact method found for user");
//        }
//    }

     //Additional endpoints for OTP verification, password reset, etc. can be added here

//    @PostMapping("/verify-otp")
//    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody OtpVerificationRequest request) {
//        // Verify the temporary token
//        if (!jwtProvider.validateTempToken(request.getTempToken())) {
//            throw new InvalidTokenException("Invalid or expired token");
//        }
//
//        String email = jwtProvider.getEmailFromTempToken(request.getTempToken());
//        User user = userRepository.findByEmail(email);
//
//        if (user == null) {
//            throw new UserNotFoundException("User not found");
//        }
//
//        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findByUser(user.getId());
//        if (twoFactorOTP == null || !twoFactorOTP.getOtp().equals(request.getOtp()) ||
//                twoFactorOTP.getExpiryTime().isBefore(LocalDateTime.now())) {
//            throw new OtpValidationException("Invalid or expired OTP");
//        }
//
//        // OTP is valid, generate a new JWT
//        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), null, List.of());
//        String jwt = jwtProvider.generateToken(auth);
//
//        AuthResponse res = new AuthResponse(jwt, true, "Two-factor authentication successful");
//        return ResponseEntity.ok(res);
//    }
}