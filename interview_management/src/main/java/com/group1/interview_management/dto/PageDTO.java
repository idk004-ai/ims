package com.group1.interview_management.dto;

import java.util.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO<T> {
     private List<T> content;
     private int number;
     private int size;
     private long totalElements;
     private int totalPages;
     private boolean first; // first page
     private boolean last; // last page
}
