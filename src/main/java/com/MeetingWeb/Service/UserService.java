package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.GroupCategoryRepository;
import com.MeetingWeb.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final GroupCategoryRepository groupCategoryRepository;

    public List<GroupCategory> getGroupCategories() {
        return groupCategoryRepository.findAll();
    }

    public void singUp(UserDto userDto, PasswordEncoder passwordEncoder) {
        //DB에서 모든 GroupCategory 가져오기
        List<GroupCategory> groupCategories = getGroupCategories();
        //User 엔티티 생성
        User user = userDto.toEntity(groupCategories, passwordEncoder);
        userRepository.save(user);
    }

}
