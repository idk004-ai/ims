package com.group1.interview_management.repositories;

import java.util.Optional;

import com.group1.interview_management.entities.Token;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    Optional<Token> findByToken(String token);
}
