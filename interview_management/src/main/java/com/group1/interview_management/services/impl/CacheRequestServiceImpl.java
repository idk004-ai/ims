package com.group1.interview_management.services.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.group1.interview_management.services.CacheRequestService;

@Service
public class CacheRequestServiceImpl implements CacheRequestService {
     @Value("${spring.cached.path}")
     private String path;
     private final Cache<String, String> requestCache;

     public CacheRequestServiceImpl() {
          this.requestCache = Caffeine.newBuilder()
                    .expireAfterWrite(30, TimeUnit.MINUTES)
                    .maximumSize(100)
                    .build();
     }

     @Override
     public void cacheRequest(String key, String requestUrl) {
          if (requestUrl.contains(path)) {
               requestCache.put(key, requestUrl);
          }

     }

     @Override
     public String getAndRemoveCachedRequest(String key) {
          String cachedUrl = requestCache.getIfPresent(key);
          if (cachedUrl != null) {
               requestCache.invalidate(key);
          }
          return cachedUrl;
     }

}
