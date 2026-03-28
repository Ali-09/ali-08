package com.example.ali_08.repository;

import com.example.ali_08.model.Record;
import com.example.ali_08.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, Long> {
    List<Record> findByUserOrderByDateDesc(User user);
    List<Record> findByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
