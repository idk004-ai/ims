package com.group1.interview_management.services;

public interface CacheRequestService {

     void cacheRequest(String key, String value);

     String getAndRemoveCachedRequest(String key);
}
