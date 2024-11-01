package com.MeetingWeb.Dto;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.User;

import com.MeetingWeb.Entity.UserSelectCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDto {
    private String userName;
    private String password;
    private String name;
    private String email;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    private String activityArea;
    private MultipartFile profileImage;
    private String gender;
    private List<Long> selectedCategoryIds;

    public User toEntity(List<GroupCategory> groupCategories, PasswordEncoder passwordEncoder, String profileImageUrl){
        User user = new User();
        user.setName(this.name);
        user.setEmail(this.email);
        user.setBirthdate(this.birthdate);
        user.setPassword(passwordEncoder.encode(this.password));
        user.setActivityArea(this.activityArea);
        user.setProfileImgUrl(profileImageUrl);
        user.setUserName(this.userName);
        user.setGender(Gender.valueOf(this.gender));
        user.setRole(Role.USER);

        // 선택한 카테고리 설정
        List<UserSelectCategory> selectedCategories = this.selectedCategoryIds.stream()
                .map(categoryId -> {
                    GroupCategory groupCategory = groupCategories.stream()
                            .filter(gc -> gc.getGroupCategoryId().equals(categoryId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
                    UserSelectCategory userSelectCategory = new UserSelectCategory();
                    userSelectCategory.setUser(user);
                    userSelectCategory.setGroupCategory(groupCategory);
                    return userSelectCategory;
                })
                .collect(Collectors.toList());

        user.setSelectedCategories(selectedCategories);

        return user;
    }
}



