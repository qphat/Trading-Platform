package com.koomi.tradingplatfrom.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.koomi.tradingplatfrom.domain.ContactMethod;
import com.koomi.tradingplatfrom.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String fullName;
    private String email;
    private String phoneNumber;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Embedded
    private TwoFactorAuth twoFactorAuth = new TwoFactorAuth();

    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

    @Enumerated(EnumType.STRING)
    private ContactMethod preferredContactMethod;

}
