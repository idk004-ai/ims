package com.group1.interview_management.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "email_period")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailPeriod {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     Integer id;
     @NotNull
     LocalDateTime createdAt;
     @NotNull
     LocalDateTime expiredAt;
     LocalDateTime accessedAt;
     boolean hasAccessed;
     @ManyToOne
     @JoinColumn(name = "user_id", nullable = false)
     User user;
     @NotNull
     String uuid;
}
