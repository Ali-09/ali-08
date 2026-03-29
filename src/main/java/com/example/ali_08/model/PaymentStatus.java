package com.example.ali_08.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "payment_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String status;

    @Column(length = 50)
    private String color;
}
