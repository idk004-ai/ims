package com.group1.interview_management.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
public class Job extends BaseEntity {
     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "job_id")
     private int jobId;

     @Column(name = "job_title", nullable = false, length = 100)
     private String title;

     @Column(name = "job_description", nullable = true, columnDefinition = "TEXT")
     private String description;

     @Column(name = "salary_from", nullable = false)
     private Double salaryFrom;

     @Column(name = "salary_to", nullable = false)
     private Double salaryTo;

     @Column(name = "start_date", nullable = false)
     private LocalDate startDate;

     @Column(name = "end_date", nullable = false)
     private LocalDate endDate;

     @Column(name = "level", nullable = false, columnDefinition = "TEXT")
     private String level;

     @Column(name = "benefits", nullable = false, columnDefinition = "TEXT")
     private String benefits;

     @Column(name = "skills", nullable = false, columnDefinition = "TEXT")
     private String skills;

     @Column(name = "working_address", nullable = false, columnDefinition = "TEXT")
     private String workingAddress;

     @Column(name = "status_job_id", nullable = false)
     private int statusJobId;

     @OneToMany(mappedBy = "job")
     List<JobApplication> jobApplications;

}
