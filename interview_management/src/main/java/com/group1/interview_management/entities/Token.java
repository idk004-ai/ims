package com.group1.interview_management.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "tbl_tokens")
public class Token {
     @Id
     @GeneratedValue
     private Integer id;
     private String token;
     private LocalDateTime createdAt;
     private LocalDateTime expiredAt;
     private LocalDateTime validatedAt;

     @ManyToOne
     @JoinColumn(name = "user_id", nullable = false)
     private User user;
}
