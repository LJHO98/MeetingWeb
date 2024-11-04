package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class GroupDto {
    private Long groupId;
    private String name;
    private String introduce;
    private String description;
    private String category;
    private String location;
    private String genderPreference;
    private Integer minAge;
    private Integer maxAge;
    private String registrationType;
    private Integer currentHeadCount;
    private Integer capacity;
    private Long createdBy;
    private MultipartFile profileImg;
    private String profileImgUrl;
    private List<String> descriptionImageUrls;

    public Groups toEntity(String profileImageUrl,User createdBy) {
        Groups group = new Groups();
        group.setName(this.name);
        group.setIntroduce(this.introduce);
        group.setDescription(this.description);
        group.setCategory(this.category);
        group.setLocation(this.location);
        group.setGenderPreference(this.genderPreference);
        group.setMinAge(this.minAge);
        group.setMaxAge(this.maxAge);
        group.setRegistrationType(this.registrationType);
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
        groupDto.minAge = groups.getMinAge();
        groupDto.maxAge = groups.getMaxAge();
        groupDto.registrationType = groups.getRegistrationType();
        groupDto.currentHeadCount = groups.getCurrentHeadCount();
        groupDto.capacity = groups.getCapacity();
        groupDto.profileImgUrl = groups.getProfileImgUrl();
        return groupDto;
    }
}
