package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.GroupCategoryRepository;
import com.MeetingWeb.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final GroupCategoryRepository groupCategoryRepository;
    private final ProfileUploadService profileUploadService;

    public List<GroupCategory> getGroupCategories() {
        return groupCategoryRepository.findAll();
    }

    public User findByUserName(String userName) {
        User user = userRepository.findByUserName(userName);
        return user;
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

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()))
                .build();
    }

}
