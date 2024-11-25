package com.group1.interview_management.services;

import com.group1.interview_management.entities.Master;

import java.util.List;
import java.util.Optional;

public interface MasterService {

    List<Master> getUserStatus(String userStatus);

    List<Master> findByCategory(String category);

    Optional<Master> findByCategoryAndValue(String category, String value);

    Master findByCategoryAndCategoryId(String category, Integer categoryId);

    List<Master> getAllJobStatuses();

  
    List<String> getAllLevelsById(String levels);

    String getListCategoryValue(String categoryList, String category);
}
