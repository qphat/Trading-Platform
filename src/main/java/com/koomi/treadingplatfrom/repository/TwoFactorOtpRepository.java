package com.koomi.treadingplatfrom.repository;

import com.koomi.treadingplatfrom.model.entity.TwoFactorOTP;
import com.koomi.treadingplatfrom.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TwoFactorOtpRepository extends JpaRepository<TwoFactorOTP, String> {
    // Phương thức tìm kiếm theo đối tượng User
    TwoFactorOTP findByUser(User user);

    // Phương thức mới tìm kiếm theo ID người dùng
    TwoFactorOTP findByUserId(Long userId);

}
