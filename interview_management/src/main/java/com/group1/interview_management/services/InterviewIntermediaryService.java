package com.group1.interview_management.services;

public interface InterviewIntermediaryService {

     /**
      * Generic method to cancel interviews based on the type of object
      * 
      * @param <T>   The type of object
      * @param clazz The class of the object
      */
     <T> void cancelInterviews(Class<T> clazz);

}
