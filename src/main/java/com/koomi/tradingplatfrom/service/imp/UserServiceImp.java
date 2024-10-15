package com.koomi.tradingplatfrom.service.imp;

import com.koomi.tradingplatfrom.config.JwtProvider;
import com.koomi.tradingplatfrom.domain.VerificationType;
import com.koomi.tradingplatfrom.exception.UserNotFoundException;
import com.koomi.tradingplatfrom.model.entity.TwoFactorAuth;
import com.koomi.tradingplatfrom.model.entity.User;
import com.koomi.tradingplatfrom.repository.UserRepository;
import com.koomi.tradingplatfrom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public class UserServiceImp implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private  JwtProvider jwtProvider;



    @Override
    public User findUserProfileByJwt(String jwt) {
        String email = jwtProvider.getEmailFromToken(jwt); // Sử dụng jwtProvider để gọi phương thức
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return user;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return null;
    }

    @Override
    public User findUserById(Long id) {
        Optional<User> user = userRepository.findById(String.valueOf(id));
        if(user.isEmpty()){
            throw new UserNotFoundException("User not found");
        }
        return user.get();
    }

    @Override
    public User enableTwoFactorAuth(VerificationType verificationType, String sendTo, User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnable(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);

        return userRepository.save(user);
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);

    }
}
