package com.group1.interview_management.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group1.interview_management.dto.UserDTO;
import com.group1.interview_management.entities.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    Optional<User> findByRefreshToken(String refreshToken);

    boolean existsByEmail(String email);

    @Query("""
                SELECT new com.group1.interview_management.dto.UserDTO(u.id,u.fullname,u.username_, u.phone,
                       r.categoryValue, s.categoryValue, u.email, g.categoryValue, u.address, u.dob, d.categoryValue, u.note)
                FROM User u
                JOIN Master s ON u.status = s.categoryId AND s.category = 'USER_STATUS'
                JOIN Master r ON u.roleId = r.categoryId AND r.category = 'USER_ROLE'
                JOIN Master g ON u.gender = g.categoryId AND g.category = 'USER_GENDER'
                JOIN Master d ON u.departmentId = d.categoryId AND d.category = 'DEPARTMENT'
                WHERE (:keyword IS NULL OR :keyword = '' OR
                       LOWER(r.categoryValue) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                       LOWER(u.username_) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                       LOWER(u.fullname) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                       LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                       LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%')))
                AND (:status IS NULL OR s.categoryId = :status)
                ORDER BY u.modifiedDate DESC
            """)
    Page<UserDTO> searchByKeyword(@Param("keyword") String keyword,
            @Param("status") Integer status,
            Pageable pageable);

    List<User> findByRoleId(int roleId);

    /**
     * Find users whose IDs are in the provided list and match the specified role(s)
     * 
     * @param userIds
     * @param roleIds
     * @return
     */
    @Query("""
            SELECT u
            FROM User u
            WHERE u.id IN (:userIds)
            AND u.roleId IN (:roleIds)
            AND u.enabled = true
            AND u.status = 1
            """)
    Optional<List<User>> findByIdsAndRoleIds(@Param("userIds") List<Integer> userIds,
            @Param("roleIds") List<Integer> roleIds);

    @Query("""
                SELECT COUNT(u) FROM User u WHERE u.username_ = :username
            """)
    int countUser(@Param("username") String username);

    boolean existsByPhone(String phone);

    List<User> findByFullname(String name);

}
