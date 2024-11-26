package com.group1.interview_management.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.group1.interview_management.repositories.UserRepository;

@Component
public class UserUtils {
    private static UserRepository userRepository;

    @Autowired
    public UserUtils(UserRepository userRepository) {
        UserUtils.userRepository = userRepository;
    }

    public static String encodeUsername(String username) {
        String[] array = username.trim().split(" ");
        String encodedUsername = array[array.length - 1];
        for (int i = 0; i < array.length - 1; i++) {
            encodedUsername += array[i].charAt(0);
        }
        int incrementalNumber = userRepository.countUser(encodedUsername);
        if (incrementalNumber > 0)
        return encodedUsername + incrementalNumber;
        else{
            return encodedUsername;
        }
    }
}
