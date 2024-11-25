package com.group1.interview_management.repositories;

import java.util.Optional;

import com.group1.interview_management.entities.EmailToken;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTokenRepository extends JpaRepository<EmailToken, Integer> {

    Optional<EmailToken> findByToken(String token);
}
