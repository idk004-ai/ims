package com.group1.interview_management;

import com.group1.interview_management.entities.Master;
import com.group1.interview_management.repositories.MasterRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableAsync
@EnableTransactionManagement
@EnableJpaRepositories
@EnableScheduling
public class InterviewManagementApplication {
	public static void main(String[] args) {
		SpringApplication.run(InterviewManagementApplication.class, args);
	}
	@Bean
	public CommandLineRunner commandLineRunner(MasterRepository masterRepository) {
		return (args) -> {
			addMasterIfNotExists(masterRepository, "USER_ROLE", "ADMIN");
			addMasterIfNotExists(masterRepository, "USER_ROLE", "MANAGER");
			addMasterIfNotExists(masterRepository, "USER_ROLE", "RECRUITER");
			addMasterIfNotExists(masterRepository, "USER_ROLE", "INTERVIEWER");

			addMasterIfNotExists(masterRepository, "USER_GENDER", "Male");
			addMasterIfNotExists(masterRepository, "USER_GENDER", "Female");
			addMasterIfNotExists(masterRepository, "USER_GENDER", "Other");

			addMasterIfNotExists(masterRepository, "USER_STATUS", "ACTIVE");
			addMasterIfNotExists(masterRepository, "USER_STATUS", "INACTIVE");

			addMasterIfNotExists(masterRepository, "DEPARTMENT", "IT");
			addMasterIfNotExists(masterRepository, "DEPARTMENT", "HR");
			addMasterIfNotExists(masterRepository, "DEPARTMENT", "Finance");
			addMasterIfNotExists(masterRepository, "DEPARTMENT", "Communication");
			addMasterIfNotExists(masterRepository, "DEPARTMENT", "Marketing");
			addMasterIfNotExists(masterRepository, "DEPARTMENT", "Accounting");

			addMasterIfNotExists(masterRepository, "SKILLS", "Java");
			addMasterIfNotExists(masterRepository, "SKILLS", "Nodejs");
			addMasterIfNotExists(masterRepository, "SKILLS", ".net");
			addMasterIfNotExists(masterRepository, "SKILLS", "C++");
			addMasterIfNotExists(masterRepository, "SKILLS", "Business Analyst");
			addMasterIfNotExists(masterRepository, "SKILLS", "Communication");

			addMasterIfNotExists(masterRepository, "HIGHEST_LEVEL", "High School");
			addMasterIfNotExists(masterRepository, "HIGHEST_LEVEL", "Bachelor's Degree");
			addMasterIfNotExists(masterRepository, "HIGHEST_LEVEL", "Master's Degree");
			addMasterIfNotExists(masterRepository, "HIGHEST_LEVEL", "PhD");

			addMasterIfNotExists(masterRepository, "INTERVIEW_STATUS", "Open");
			addMasterIfNotExists(masterRepository, "INTERVIEW_STATUS", "Invited");
			addMasterIfNotExists(masterRepository, "INTERVIEW_STATUS", "Interviewed");
			addMasterIfNotExists(masterRepository, "INTERVIEW_STATUS", "Closed");
			addMasterIfNotExists(masterRepository, "INTERVIEW_STATUS", "Cancelled");

			addMasterIfNotExists(masterRepository, "INTERVIEW_RESULT", "N/A");
			addMasterIfNotExists(masterRepository, "INTERVIEW_RESULT", "Passed");
			addMasterIfNotExists(masterRepository, "INTERVIEW_RESULT", "Failed");

			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Open");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Banned");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Waiting for interview");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Waiting for approval");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Waiting for response");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Passed Interview");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Approved Offer");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Rejected Offer");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Accepted Offer");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Declined Offer");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Cancelled Offer");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Failed Interview");
			addMasterIfNotExists(masterRepository, "CANDIDATE_STATUS", "Cancelled interview");

			addMasterIfNotExists(masterRepository, "CONTRACT_TYPE", "Trial 2 months");
			addMasterIfNotExists(masterRepository, "CONTRACT_TYPE", "Trainee 3 months");
			addMasterIfNotExists(masterRepository, "CONTRACT_TYPE", "1 year");
			addMasterIfNotExists(masterRepository, "CONTRACT_TYPE", "3 years");
			addMasterIfNotExists(masterRepository, "CONTRACT_TYPE", "Unlimited");

			addMasterIfNotExists(masterRepository, "OFFER_STATUS", "Waiting for approval");
			addMasterIfNotExists(masterRepository, "OFFER_STATUS", "Approved offer");
			addMasterIfNotExists(masterRepository, "OFFER_STATUS", "Rejected offer");
			addMasterIfNotExists(masterRepository, "OFFER_STATUS", "Waiting for response");
			addMasterIfNotExists(masterRepository, "OFFER_STATUS", "Accepted offer");
			addMasterIfNotExists(masterRepository, "OFFER_STATUS", "Declined offer");
			addMasterIfNotExists(masterRepository, "OFFER_STATUS", "Cancelled");

			addMasterIfNotExists(masterRepository, "POSITION", "Backend Developer");
			addMasterIfNotExists(masterRepository, "POSITION", "Business Analyst");
			addMasterIfNotExists(masterRepository, "POSITION", "Tester");
			addMasterIfNotExists(masterRepository, "POSITION", "HR");
			addMasterIfNotExists(masterRepository, "POSITION", "Project Manager");
			addMasterIfNotExists(masterRepository, "POSITION", "Not available");

			addMasterIfNotExists(masterRepository, "BENEFIT", "Lunch");
			addMasterIfNotExists(masterRepository, "BENEFIT", "25-day leave");
			addMasterIfNotExists(masterRepository, "BENEFIT", "Healthcare insurance");
			addMasterIfNotExists(masterRepository, "BENEFIT", "Hybrid working");
			addMasterIfNotExists(masterRepository, "BENEFIT", "Travel");

			addMasterIfNotExists(masterRepository, "LEVEL", "Fresher");
			addMasterIfNotExists(masterRepository, "LEVEL", "Junior");
			addMasterIfNotExists(masterRepository, "LEVEL", "Senior");
			addMasterIfNotExists(masterRepository, "LEVEL", "Leader");
			addMasterIfNotExists(masterRepository, "LEVEL", "Manager");
			addMasterIfNotExists(masterRepository, "LEVEL", "Vice Head");

			addMasterIfNotExists(masterRepository, "JOB_STATUS", "Draft");
			addMasterIfNotExists(masterRepository, "JOB_STATUS", "Open");
			addMasterIfNotExists(masterRepository, "JOB_STATUS", "Closed");
		};
	}

	private void addMasterIfNotExists(MasterRepository masterRepository, String category, String categoryValue) {
		if (masterRepository.findByCategoryAndCategoryValue(category, categoryValue).isEmpty()) {
			Integer nextCategoryId = masterRepository.findMaxCategoryIdByCategory(category)
					.map(maxId -> maxId + 1)
					.orElse(1);

			Master master = Master.builder()
					.category(category)
					.categoryId(nextCategoryId)
					.categoryValue(categoryValue)
					.build();

			masterRepository.save(master);
		}
	}
}
