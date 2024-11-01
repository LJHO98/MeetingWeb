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
    private final ProfileUploadService profileUploadService;

    public List<GroupCategory> getGroupCategories() {
        return groupCategoryRepository.findAll();
    }

    public void singUp(UserDto userDto, PasswordEncoder passwordEncoder) {
        try {
            String profileImageUrl = profileUploadService.saveUserProfile(userDto.getProfileImage());
            //DB에서 모든 GroupCategory 가져오기
            List<GroupCategory> groupCategories = getGroupCategories();
            //User 엔티티 생성
            User user = userDto.toEntity(groupCategories, passwordEncoder, profileImageUrl);
            userRepository.save(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
