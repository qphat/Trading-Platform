package com.koomi.tradingplatfrom.model.entity;


import com.koomi.tradingplatfrom.domain.WalletTransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class WalletTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Wallet wallet;

    private WalletTransactionType type;

    private LocalDateTime date;

    private String tranferId;

    private String purpose;

    private double amount;


}
