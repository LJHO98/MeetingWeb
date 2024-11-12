package com.MeetingWeb.Repository;

import com.MeetingWeb.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);

    User findByEmail(String email);

    User findByPassword(String password);


}
    