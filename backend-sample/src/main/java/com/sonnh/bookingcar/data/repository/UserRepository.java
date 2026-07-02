package com.sonnh.bookingcar.data.repository;

import com.sonnh.bookingcar.data.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);

    @Query("SELECT u FROM User u WHERE u.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.documents d WHERE u.role.name = 'DRIVER' AND u.audit.isActive = true")
    List<User> findAllActiveDriversWithDocuments();

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.audit.isActive = true")
    List<User> findUserByRoleName(@Param("roleName") String roleName);

    @Query("SELECT u FROM User u WHERE u.role.name = :roleName AND u.id = :id")
    Optional<User> findUserByRoleNameAndId(@Param("roleName") String roleName, @Param("id") UUID id);

    @Query("SELECT u FROM User u WHERE u.username = :username OR u.phone = :phone")
    Optional<User> findByUsernameOrPhone(@Param("username") String username, @Param("phone") String phone);
}
