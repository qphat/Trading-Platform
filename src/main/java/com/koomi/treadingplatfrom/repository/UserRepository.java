package com.koomi.treadingplatfrom.repository;

import com.koomi.treadingplatfrom.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
     User findByEmail(String email);


}
