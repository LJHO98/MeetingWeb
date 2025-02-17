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

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class UserDto {
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영어, 숫자만 사용 가능합니다.")
    @Size(min = 4, max = 10, message = "아이디는 4~10자입니다.")
    private String userName;

    private Long userId;

    @Size(min = 8, max = 12, message = "비밀번호는 8~12자입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[\\d!@#$%^&*()_+])[A-Za-z\\d!@#$%^&*()_+]{8,12}$", message = "비밀번호는 영어 대소문자와 숫자 또는 특수문자를 조합하여 8자 이상 12자 이하로 입력해 주세요.")
    private String password;
    @NotBlank(message = "이름은 필수 입니다.")
    private String name;

    private String email;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthdate;
    private String activityArea;
    private MultipartFile profileImage;
    private String profileImgUrl;
    private String gender;
    private List<Long> selectedCategoryIds;
    private List<String> selectedCategoryNames;

    public User toEntity(List<GroupCategoryDto> groupCategories, PasswordEncoder passwordEncoder, String profileImageUrl){
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
                    GroupCategoryDto groupCategory = groupCategories.stream()
                            .filter(gc -> gc.getGroupCategoryId().equals(categoryId))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
                    UserSelectCategory userSelectCategory = new UserSelectCategory();
                    userSelectCategory.setUser(user);
                    userSelectCategory.setGroupCategory(GroupCategoryDto.toEntity(groupCategory));
                    return userSelectCategory;
                })
                .collect(Collectors.toList());

        user.setSelectedCategories(selectedCategories);

        return user;
    }
    public static UserDto of(User user) {
        UserDto userDto = new UserDto();
        userDto.setUserId(user.getId());

        userDto.setName(user.getName());
        userDto.setUserName(user.getUserName());
        userDto.setBirthdate(user.getBirthdate());
        userDto.setActivityArea(user.getActivityArea());
        userDto.setGender(user.getGender().toString());
        userDto.setProfileImgUrl(user.getProfileImgUrl());
        // User의 selectedCategories에서 카테고리 ID를 추출하여 UserDto의 selectedCategoryIds에 설정
        List<String> categoryIds = user.getSelectedCategories().stream()
                .map(userSelectCategory -> userSelectCategory.getGroupCategory().getCategory())
                .collect(Collectors.toList());
        userDto.setSelectedCategoryNames(categoryIds);
        return userDto;
    }

}


