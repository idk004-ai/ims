package com.group1.interview_management.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group1.interview_management.entities.EmailPeriod;

public interface EmailPeriodRepository extends JpaRepository<EmailPeriod,Integer>{
    Optional<EmailPeriod> findByUuid(String uuid);
}
