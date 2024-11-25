package com.group1.interview_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group1.interview_management.entities.Master;

import org.springframework.stereotype.Repository;


@Repository
public interface MasterRepository extends JpaRepository<Master, Integer> {
     @Query("""
               SELECT r FROM Master r
               WHERE r.category = :category
               AND r.categoryValue = :value
               """)
     Optional<Master> findByCategoryAndCategoryValue(@Param("category") String category, @Param("value") String categoryValue);

     @Query("SELECT MAX(m.categoryId) FROM Master m WHERE m.category = :category")
     Optional<Integer> findMaxCategoryIdByCategory(@Param("category") String category);

     List<Master> findByCategory(String category);

     @Query("SELECT m FROM Master m WHERE m.category = :category AND m.categoryId = :categoryId")
     Optional<Master> findByCategoryAndCategoryId(@Param("category") String category,
               @Param("categoryId") Integer categoryId);

     @Query("SELECT m FROM Master m WHERE m.category = :category AND m.categoryId = :categoryId")
     Master findByCategoryValueByCategoryId(@Param("category") String category, @Param("categoryId") Integer categoryId);
}
