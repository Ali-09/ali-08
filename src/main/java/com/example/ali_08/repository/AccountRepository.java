package com.example.ali_08.repository;

import com.example.ali_08.model.Account;
import com.example.ali_08.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByUser(User user);
}
