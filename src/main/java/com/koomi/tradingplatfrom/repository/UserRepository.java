package com.koomi.tradingplatfrom.repository;

import com.koomi.tradingplatfrom.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
     User findByEmail(String email);


}
