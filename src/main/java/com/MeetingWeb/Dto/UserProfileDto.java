package com.MeetingWeb.Dto;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.Role;
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
public class UserProfileDto {
    private String userName; // 사용자 이름 (유저네임)
    private String name; // 사용자 이름
    private String email; // 사용자 이메일 주소
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate; // 사용자 생년월일
    private String activityArea; // 사용자의 활동 지역
    private MultipartFile profileImg; // 업로드된 프로필 사진 파일
    private String profileImgUrl; // 저장된 프로필 사진 경로 또는 URL
    private Gender gender; // 사용자 성별
    private List<Long> selectedCategoryIds; // 사용자 선택 카테고리

    // User 엔티티 업데이트 메서드
    public void updateEntity(User user) {
        user.setName(this.name);
        user.setEmail(this.email);
        user.setBirthdate(this.birthDate);
        user.setActivityArea(this.activityArea);
        user.setGender(this.gender);
        if (this.profileImgUrl != null) {
            user.setProfileImgUrl(this.profileImgUrl);
        }
    }

    // Entity to DTO 변환 메서드
    public static UserProfileDto of(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setUserName(user.getUserName());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthdate());
        dto.setActivityArea(user.getActivityArea());
        dto.setProfileImgUrl(user.getProfileImgUrl());
        dto.setGender(user.getGender());
        dto.setSelectedCategoryIds(user.getSelectedCategories().stream()
                .map(usc -> usc.getGroupCategory().getGroupCategoryId())
                .collect(Collectors.toList()));
        return dto;
    }

}
