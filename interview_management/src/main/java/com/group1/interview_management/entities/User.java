package com.group1.interview_management.entities;

import java.util.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority; 
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails, Principal {
     @Id
     @Column(name = "user_id")
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Integer id;

     @Column(name = "refresh_token", nullable = true)
     private String refreshToken;

     @Column(unique = true)
     private String email;

     @Column(nullable = false)
     private String password;

     @Column(nullable = false)
     private String fullname;

     @Column(name = "department_id")
     private int departmentId;

     @Column(name = "role_id")
     private int roleId;

     @Column(name = "status_id", nullable = false)
     private int status;

     @Column(name = "gender_id", nullable = false)
     private int gender;

     @Column(nullable = false)
     private String phone;

     @Column(nullable = false)
     private LocalDate dob;

     @Column(nullable = false)
     private String address;

     private boolean enabled;

     private String note;
     
     @Column(name = "username")
     private String username_;

     @OneToMany(mappedBy = "interviewer")
     private Set<InterviewAssignment> interviewAssignments;

     @OneToMany(mappedBy = "createdBy")
     private List<Interview> interviews;

     @OneToMany(mappedBy = "createdBy")
     private List<Candidate> candidates;

     @OneToMany(mappedBy = "createdBy")
     private List<Interview> offers;

     @OneToMany(mappedBy = "approver")
     private List<Interview> approvedOffers;

     @OneToMany(mappedBy = "createdBy")
     private List<Job> jobs;

     @CreatedDate
     @Column(nullable = false, updatable = false)
     private LocalDateTime createdDate;

     @LastModifiedDate
     @Column(insertable = false)
     private LocalDateTime modifiedDate;

     @Override
     public String getName() {
          return this.fullname;
     }

     @Override
     public String getUsername() {
          return this.email;
     }

     @Override
     public Collection<? extends GrantedAuthority> getAuthorities() {
          List<GrantedAuthority> authorities = new ArrayList<>();
              switch (roleId) {
                  case 1:
                      authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                      break;
                  case 2:
                      authorities.add(new SimpleGrantedAuthority("ROLE_MANAGER"));
                      break;
                  case 3:
                      authorities.add(new SimpleGrantedAuthority("ROLE_RECRUITER"));
                      break;
                  case 4: 
                      authorities.add(new SimpleGrantedAuthority("ROLE_INTERVIEWER"));
                      break;
              }
              return authorities;
          }
      

     @Override
     public String getPassword() {
          return this.password;
     }

}
