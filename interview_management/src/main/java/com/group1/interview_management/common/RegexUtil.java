package com.group1.interview_management.common;

public class RegexUtil {
    public final static String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?~`]{7,}$";
    public final static String PHONE_REGEX = "^(0[1-9][0-9]{0,13}|\\+84[1-9][0-9]{0,13})$";
    public final static String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(\\.[a-zA-Z]{2,})*$";
    public  final static String INVALID_NAME_REGEX = "(?s).*\\d.*";
}
