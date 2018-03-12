package com.example.lavaeolus.dao;

import com.example.lavaeolus.dao.domain.LavaeolusUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<LavaeolusUser, Long> {
    Optional<LavaeolusUser> findOneByUsername(String username);
}
