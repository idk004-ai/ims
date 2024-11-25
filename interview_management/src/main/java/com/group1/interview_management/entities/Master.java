package com.group1.interview_management.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "masters", 
    uniqueConstraints = @UniqueConstraint(columnNames = {"category", "category_id"}),
    indexes = @Index(columnList = "category, category_id")
)
public class Master {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;

     @Column(name = "category", nullable = false, length = 50)
     private String category;

     @Column(name = "category_id", nullable = false)
     private Integer categoryId;

     @Column(name = "category_value", nullable = false, length = 255)
     private String categoryValue;

}
