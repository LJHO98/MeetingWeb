package com.MeetingWeb.Repository;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);

    User findByEmail(String email);

    @Query("select count(u) from User u where month(u.createdAt)= :m")
    int countUserByCreatedAt(@Param("m") int month);

    @Query("SELECT COUNT(u) FROM User u WHERE u.gender = :gender")
    int countUserByGender(Gender gender);

    // 전체 회원 수 카운트
    @Query("SELECT COUNT(u) FROM User u")
    long countTotalUsers();

    Optional<User> findById(Long userId);









}
    