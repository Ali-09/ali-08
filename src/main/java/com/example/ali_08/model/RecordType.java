package com.example.ali_08.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "record_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecordType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;
}
