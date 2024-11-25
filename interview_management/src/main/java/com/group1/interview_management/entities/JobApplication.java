package com.group1.interview_management.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "job_application")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JobApplication {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;

     @ManyToOne(fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "job_id", referencedColumnName = "job_id")
     private Job job;

     @ManyToOne(fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "candidate_id", referencedColumnName = "candidate_id")
     private Candidate candidate;
}    
