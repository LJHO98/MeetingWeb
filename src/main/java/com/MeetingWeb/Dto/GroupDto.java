package com.MeetingWeb.Dto;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.RegistType;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class GroupDto {
    private Long groupId;

    @NotNull(message="필수 입력입니다.")
    @Size(min = 5, max=10 ,message="최소 5자에서 최대 10자 사이로 입력해주세요")
    private String name;

    @NotNull(message="필수 입력입니다.")
    @Size(min = 5, max=15 ,message="최소 5자에서 최대 15자 사이로 입력해주세요")
    private String introduce;

    @NotNull(message="필수 입력입니다.")
    @Size(min = 0, max=500 , message="500자 이하로 입력해주세요")
    private String description;

    @NotNull(message="필수 선택입니다.")
    private String category;

    private String location;

    @NotNull(message="필수 선택입니다.")
    private Gender genderPreference;
//    private Integer minAge;
//    private Integer maxAge;
    @NotNull(message="필수 선택입니다.")
    private RegistType registrationType;
    private Integer currentHeadCount;

    @Min(1)
    @Max(30)
    @NotNull(message="필수 선택입니다.")
    private Integer capacity;
    private Long createdBy;

    @NotNull(message="프로필 이미지가 필요합니다.")
    private MultipartFile profileImg;
    private String profileImgUrl;
//    private List<String> descriptionImageUrls;

    public Groups toEntity(String profileImageUrl,User createdBy) {
        Groups group = new Groups();
        group.setName(this.name);
        group.setIntroduce(this.introduce);
        group.setDescription(this.description);
        group.setCategory(this.category);
        group.setLocation(this.location);
        group.setGenderPreference(this.genderPreference);
//        group.setMinAge(this.minAge);
//        group.setMaxAge(this.maxAge);
        group.setRegistrationType(this.getRegistrationType());
        group.setCurrentHeadCount(this.currentHeadCount);
        group.setCapacity(this.capacity);
        group.setProfileImgUrl(profileImageUrl);
        group.setCreatedBy(createdBy);
        return group;
    }

    public static GroupDto of(Groups groups) {
        GroupDto groupDto = new GroupDto();
        groupDto.name = groups.getName();
        groupDto.introduce = groups.getIntroduce();
        groupDto.description = groups.getDescription();
        groupDto.category = groups.getCategory();
        groupDto.location = groups.getLocation();
        groupDto.genderPreference = groups.getGenderPreference();
//        groupDto.minAge = groups.getMinAge();
//        groupDto.maxAge = groups.getMaxAge();
        groupDto.registrationType = groups.getRegistrationType();
        groupDto.currentHeadCount = groups.getCurrentHeadCount();
        groupDto.capacity = groups.getCapacity();
        groupDto.profileImgUrl = groups.getProfileImgUrl();
        return groupDto;
    }
}
