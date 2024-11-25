package com.group1.interview_management.services.impl;

import com.group1.interview_management.common.ConstantUtils;

import java.util.*;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.group1.interview_management.dto.MasterDTO;
import com.group1.interview_management.entities.Master;
import com.group1.interview_management.repositories.MasterRepository;
import com.group1.interview_management.services.MasterService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@PropertySource("classpath:messages.properties")

public class MasterServiceImpl implements MasterService {
    MasterRepository masterRepository;
    static String INTERVIEW_STATUS = "INTERVIEW_STATUS";
    ModelMapper mapper;

    @Override
    public List<Master> findByCategory(String category) {
        return masterRepository.findByCategory(category);
    }

    public List<MasterDTO> getAllInterviewStatus() {
        List<Master> masters = masterRepository.findByCategory(INTERVIEW_STATUS);
        return masters.stream().map(master -> mapper.map(master, MasterDTO.class)).toList();
    }

    @Override
    public Optional<Master> findByCategoryAndValue(String category, String value) {
        return masterRepository.findByCategoryAndCategoryValue(category, value);
    }

    @Override
    public List<Master> getUserStatus(String userStatus) {
        return masterRepository.findByCategory(ConstantUtils.USER_STATUS);
    }

    public List<Master> getAllJobStatuses() {
        return masterRepository.findByCategory(ConstantUtils.JOB_STATUS);
    }

    public List<String> getAllLevelsById(String levels) {
        List<String> result = new ArrayList<>();
        for (String level : levels.split(",")) {
            result.add(masterRepository
                    .findByCategoryValueByCategoryId(ConstantUtils.LEVEL, Integer.parseInt(level.trim()))
                    .getCategoryValue());
        }
        return result;
    }

    /**
     * Get all category value
     *
     * @param categoryList all category id
     * @param category     category
     * @return string of category value
     */
    @Override
    public String getListCategoryValue(String categoryList, String category) {
        String[] listId = categoryList.split(",");

        // Get all skills
        List<Master> allCategory = findByCategory(category);
        // Convert list to map
        Map<Integer, String> skillMap = allCategory.stream()
                .collect(Collectors.toMap(Master::getCategoryId, Master::getCategoryValue));

        // Convert category id to category value
        StringBuilder result = new StringBuilder();
        for (String categoryId : listId) {
            int id = Integer.parseInt(categoryId.trim());
            String categoryValue = skillMap.get(id);
            if (categoryValue != null) {
                result.append(categoryValue).append(", ");
            }
        }

        // Remove the last ", "
        if (!result.isEmpty()) {
            result.delete(result.length() - 2, result.length());
        }
        return result.toString();
    }

    @Override
    public Master findByCategoryAndCategoryId(String category, Integer categoryId) {
        return masterRepository.findByCategoryAndCategoryId(category, categoryId).get();
    }

}
