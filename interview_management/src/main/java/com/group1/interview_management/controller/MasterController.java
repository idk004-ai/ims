package com.group1.interview_management.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import com.group1.interview_management.dto.MasterDTO;
import com.group1.interview_management.services.impl.MasterServiceImpl;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/master")
@RequiredArgsConstructor
public class MasterController {
     private final MasterServiceImpl masterServiceImpl;

     @GetMapping("/interview-status")
     public ResponseEntity<List<MasterDTO>> getInterviewStatus() {
          return ResponseEntity.ok(masterServiceImpl.getAllInterviewStatus());
     }

}
