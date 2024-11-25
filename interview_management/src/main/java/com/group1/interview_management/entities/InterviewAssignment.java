package com.group1.interview_management.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "interviewer_assignments")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class InterviewAssignment {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;

     @ManyToOne(fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "interviewer_id", referencedColumnName = "user_id")
     private User interviewer;

     @ManyToOne(fetch = FetchType.LAZY, optional = false)
     @JoinColumn(name = "interview_id", referencedColumnName = "interview_id")
     private Interview interview;

}
