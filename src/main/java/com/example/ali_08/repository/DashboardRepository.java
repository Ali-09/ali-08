package com.example.ali_08.repository;

import com.example.ali_08.model.Dashboard;
import com.example.ali_08.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
    List<Dashboard> findByUser(User user);
}
