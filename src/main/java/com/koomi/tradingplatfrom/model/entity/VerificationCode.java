package com.koomi.tradingplatfrom.model.entity;

import com.koomi.tradingplatfrom.domain.VerificationType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String opt;

    @OneToOne
    private User user;

    private String email;

    private String mobileNumber;

    private VerificationType verificationType;

}
