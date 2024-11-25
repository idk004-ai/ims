package com.group1.interview_management.services.impl.Job;

import com.group1.interview_management.entities.Job;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BatchJob {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void saveJobsInBatch(List<Job> jobs) {
        int batchSize = 30; // Kích thước batch (tùy chỉnh theo nhu cầu)

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            entityManager.persist(job); // Thêm job vào persistence context

            // Flush và clear sau khi đạt đến kích thước batch
            if (i % batchSize == 0 && i > 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }

        // Flush và clear những đối tượng còn lại
        entityManager.flush();
        entityManager.clear();
    }
}
