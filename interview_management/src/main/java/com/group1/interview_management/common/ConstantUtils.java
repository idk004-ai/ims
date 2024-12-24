package com.group1.interview_management.common;

public class ConstantUtils {
    // Constants master
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_RECRUITER = "ROLE_RECRUITER";
    public static final String ROLE_INTERVIEWER = "ROLE_INTERVIEWER";
    public static final String CANDIDATE_STATUS = "CANDIDATE_STATUS";
    public static final String POSITION = "POSITION";
    public static final String GENDER = "USER_GENDER";
    public static final String USER_ROLE = "USER_ROLE";
    public static final String USER_STATUS = "USER_STATUS";
    public static final String DEPARTMENT = "DEPARTMENT";
    public static final String SKILLS = "SKILLS";
    public static final String HIGHEST_LEVEL = "HIGHEST_LEVEL";
    public static final String INTERVIEW_STATUS = "INTERVIEW_STATUS";
    public static final String INTERVIEW_RESULT = "INTERVIEW_RESULT";
    public static final String CONTRACT_TYPE = "CONTRACT_TYPE";
    public static final String OFFER_STATUS = "OFFER_STATUS";
    public static final String BENEFIT= "BENEFIT";
    public static final String LEVEL = "LEVEL";
    public static final String JOB_STATUS = "JOB_STATUS";
    public static final int PAGE_SIZE = 10;


    //Constants for user status
    public static final String USER_ACTIVE = "ACTIVE";
    public static final String USER_INACTIVE = "INACTIVE";
    public static final String USER_GENDER_MALE_CATEGORY_ID = "1";
    public static final String USER_GENDER_FEMALE_CATEGORY_ID = "2";
    public static final String USER_GENDER_OTHER_CATEGORY_ID = "3";
    public static final Integer USER_ACTIVE_ID = 1;
    public static final Integer USER_INACTIVE_ID = 2;
    
    // Constants for candidate status
    public static final String STATUS_OPEN="Open";
    public static final String STATUS_BANNED="Banned";

    // Constants for user role
    public static final Integer CANDIDATE_ROLE = 0;
    public static final Integer ADMIN_ROLE = 1;
    public static final Integer MANAGER_ROLE = 2;
    public static final Integer RECRUITER_ROLE = 3;
    public static final Integer INTERVIEWER_ROLE = 4;

    // Constants for job
    public static final Integer JOB_DRAFT = 1;
    public static final Integer JOB_OPEN = 2;
    public static final Integer JOB_CLOSE = 3;
    public static final String JOB_1 = "Draft";
    public static final String JOB_2 = "Open";
    public static final String JOB_3 = "Close";

    // Constants for candidate
    public static final Integer CANDIDATE_OPEN = 1;
    public static final Integer CANDIDATE_BANNED = 2;
    public static final Integer CANDIDATE_WAITING_FOR_INTERVIEW = 3;
    public static final Integer CANDIDATE_WAITING_FOR_APPROVAL = 4;
    public static final Integer CANDIDATE_WAITING_FOR_RESPONSE = 5;
    public static final Integer CANDIDATE_PASSED_INTERVIEW = 6;
    public static final Integer CANDIDATE_APPROVED_OFFER = 7;
    public static final Integer CANDIDATE_REJECTED_OFFER = 8;
    public static final Integer CANDIDATE_ACCEPTED_OFFER = 9;
    public static final Integer CANDIDATE_DECLINED_OFFER = 10;
    public static final Integer CANDIDATE_CANCELLED_OFFER = 11;
    public static final Integer CANDIDATE_FAILED_INTERVIEW = 12;
    public static final Integer CANDIDATE_CANCELLED_INTERVIEW = 13;

    // Constants for interview
    public static final Integer INTERVIEW_STATUS_OPEN = 1;
    public static final Integer INTERVIEW_STATUS_INVITED = 2;
    public static final Integer INTERVIEW_STATUS_INTERVIEWED = 3;
    public static final Integer INTERVIEW_STATUS_CLOSED = 4;
    public static final Integer INTERVIEW_STATUS_CANCELLED = 5;
    public static final Integer INTERVIEW_RESULT_NA = 1;
    public static final Integer INTERVIEW_RESULT_PASSED = 2;
    public static final Integer INTERVIEW_RESULT_FAILED = 3;

    // Constants for offer
    public static final Integer OFFER_STATUS_WAITING_FOR_APPROVAL = 1;
    public static final Integer OFFER_STATUS_APPROVED_OFFER = 2;
    public static final Integer OFFER_STATUS_REJECTED_OFFER = 3;
    public static final Integer OFFER_STATUS_WAITING_FOR_RESPONSE = 4;
    public static final Integer OFFER_STATUS_ACCEPTED_OFFER = 5;
    public static final Integer OFFER_STATUS_DECLINED_OFFER = 6;
    public static final Integer OFFER_STATUS_CANCELLED = 7;


    // Constants for recruiter
    public static final String RECRUITER = "RECRUITER";
    // Constants for manager
    public static final String MANAGER = "MANAGER";
    // Path to candidate_list.html
    public static final String CANDIDATE_LIST = "candidate/candidate_list";
    // Path to create_candidate.html
    public static final String CREATE_CANDIDATE = "candidate/create_candidate";
    // Path to candidate_information.html
    public static final String CANDIDATE_INFORMATION = "candidate/candidate_information";
    // Path to candidate_update.html
    public static final String CANDIDATE_UPDATE = "candidate/edit_candidate";

    //Constant for email subject
    public static final String PASSWORD_RESET = "Password Reset";
    public static final String INTERVIEW_SCHEDULE_REMINDER = "Interview Schedule Reminder";
    public static final String INTERVIEW_CANCELLATION_NOTICE = "Interview Cancellation Notice";
    public static final String REMINDER_OFFER = "Offer Reminder";
    // Common Fields
    public static final String ERROR = "error";

}
