package com.koomi.tradingplatfrom.model.entity;


import com.koomi.tradingplatfrom.domain.PaymentOrderStatus;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long aount;

    private PaymentOrderStatus paymentOrderStatus;

    @ManyToOne
    private User user;

}
