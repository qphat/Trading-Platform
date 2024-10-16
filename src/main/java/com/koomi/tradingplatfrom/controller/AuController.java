package com.koomi.tradingplatfrom.controller;

import com.koomi.tradingplatfrom.Utils.OtpUtils;
import com.koomi.tradingplatfrom.config.JwtProvider;
import com.koomi.tradingplatfrom.exception.EmailAlreadyExistsException;
import com.koomi.tradingplatfrom.exception.InvalidCredentialsException;
import com.koomi.tradingplatfrom.exception.OtpValidationException;
import com.koomi.tradingplatfrom.exception.UserNotFoundException;
import com.koomi.tradingplatfrom.model.entity.TwoFactorOTP;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.repository.UserRepository;
import com.koomi.tradingplatfrom.response.AuthResponse;
import com.koomi.tradingplatfrom.service.imp.EmailService;
import com.koomi.tradingplatfrom.service.TwoFactorOtpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailService emailService;

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


    // Gửi OTP sau khi tạo OTP trong handleTwoFactorAuth()
    private ResponseEntity<AuthResponse> handleTwoFactorAuth(User user) {
        String otp = OtpUtils.generateOTP();
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findByUser(user.getId());

        if (twoFactorOTP == null) {
            twoFactorOTP = new TwoFactorOTP();
            twoFactorOTP.setUser(user);
        }

        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        twoFactorOtpService.save(twoFactorOTP);

        // Gửi OTP tới email của người dùng
        emailService.sendVerificationEmail(user.getEmail(), otp);

        AuthResponse res = new AuthResponse();
        res.setMessage("Two-factor authentication is required. An OTP has been sent to your registered email.");
        res.setTwoFactorAuthEnabled(true);
        res.setRequireOtp(true);

        String tempToken = jwtProvider.generateTempToken(user.getEmail());
        res.setTempToken(tempToken);

        return ResponseEntity.ok(res);
    }

    @GetMapping("/verify-otp/{otp}")
    public ResponseEntity<AuthResponse> verifyOtp(@PathVariable String otp, @RequestParam String id) {
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findByUser(Long.parseLong(id));
        if (twoFactorOtpService.verifyTwoFactorOTP(twoFactorOTP, otp)) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two-factor authentication successful");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            return ResponseEntity.ok(res);
        }

        throw new OtpValidationException("Invalid or expired OTP");
    }
}