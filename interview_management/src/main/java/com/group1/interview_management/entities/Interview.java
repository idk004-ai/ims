package com.group1.interview_management.entities;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "interviews")
public class Interview extends BaseEntity {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     @Column(name = "interview_id")
     private Integer interviewId;

     @Column(name = "interview_title", length = 100, nullable = false)
     private String title;

     @Column(name = "schedule_time", nullable = false)
     private LocalDate schedule;

     @Column(name = "start_time", nullable = false)
     private LocalTime startTime;

     @Column(name = "end_time", nullable = false)
     private LocalTime endTime;

     @Column(name = "interview_location", length = 100, nullable = true)
     private String location;

     @ManyToOne(fetch = FetchType.EAGER, optional = false)
     @JoinColumn(name = "candidate_id", referencedColumnName = "candidate_id")
     private Candidate candidate;

     @ManyToOne(fetch = FetchType.EAGER, optional = false)
     @JoinColumn(name = "job_id", referencedColumnName = "job_id")
     private Job job;

     @Column(name = "meeting_id", columnDefinition = "TEXT", nullable = true
     )
     private String meetingId;

     @Column(name = "interview_note", columnDefinition = "TEXT")
     private String interviewNote;

     @Column(name = "status_interview_id", nullable = false)
     private int statusInterviewId;

     @Column(name = "result_interview_id", nullable = true)
     private Integer resultInterviewId;

     @OneToMany(mappedBy = "interview", fetch = FetchType.EAGER)
     private Set<InterviewAssignment> interviewAssignments;

     /// Offer
     @Column(name = "offer_department", nullable = false)
     private int offerdepartment;

     @Column(name = "status_offer_id", nullable = false)
     private int statusOfferId;

     @Column(name = "contract_type_id", nullable = false)
     private int contractTypeId;     

     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "offer_creator", referencedColumnName = "user_id", nullable = true)
     private User offerCreator;
     
     // private Master department; -> get by User

     @Column(name = "due_date", nullable = false)
     private LocalDate dueDate;

     @Column(name = "contract_from", nullable = false)
     private LocalDate contractFrom; 
     
     @Column(name = "contract_to", nullable = false)
     private LocalDate contractTo; 

     @Column(name = "basic_salary", nullable = false)
     private Double salary;

     @Column(name = "offer_note", columnDefinition = "TEXT")
     private String offerNote;

     @ManyToOne(fetch = FetchType.EAGER)
     @JoinColumn(name = "manager_id", referencedColumnName = "user_id")
     private User approver;

}
