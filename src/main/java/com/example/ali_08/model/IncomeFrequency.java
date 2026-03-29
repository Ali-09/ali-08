package com.example.ali_08.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "income_frequencies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeFrequency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g., "Weekly", "Biweekly", "Monthly"

    private Integer days; // number of days for this frequency
}
