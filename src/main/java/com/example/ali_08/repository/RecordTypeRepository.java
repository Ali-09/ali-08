package com.example.ali_08.repository;

import com.example.ali_08.model.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RecordTypeRepository extends JpaRepository<RecordType, Long> {
    Optional<RecordType> findByName(String name);
}
