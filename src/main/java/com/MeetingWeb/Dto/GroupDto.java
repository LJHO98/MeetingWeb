package com.MeetingWeb.Dto;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.RegistType;
import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.GroupMember;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@RequiredArgsConstructor
public class GroupDto {
    private Long groupId;

    @NotNull(message="필수 입력입니다.")
    @Size(min = 5, max=10 ,message="최소 5자에서 최대 10자 사이로 입력해주세요")
    private String name;

    @NotNull(message="필수 입력입니다.")
    @Size(min = 5, max=15 ,message="최소 5자에서 최대 15자 사이로 입력해주세요")
    private String introduce;

    @NotNull(message="필수 입력입니다.")
//    @Size(min = 0, max=1000 , message="1000자 이하로 입력해주세요")
    private String description;

    @NotNull(message="필수 선택입니다.")
    private Long category;

    private String location;

    @NotNull(message="필수 선택입니다.")
    private Gender genderPreference;
    @NotNull(message="필수 선택입니다.")
    private RegistType registrationType;
    private Integer currentHeadCount;

    @Min(1)
    @Max(30)
    @NotNull(message="필수 선택입니다.")
    private Integer capacity;
    private Long createdBy;

    private String categoryName;

    @NotNull(message="프로필 이미지가 필요합니다.")
    private MultipartFile profileImg;
    private String profileImgUrl;
    private List<UserDto> members; // 회원 목록



    public Groups toEntity(String profileImageUrl, User createdBy, GroupCategory groupCategory) {
        Groups group = new Groups();
        group.setName(this.name);
        group.setIntroduce(this.introduce);
        group.setDescription(this.description);
        group.setCategory(groupCategory);
        group.setLocation(this.location);
        group.setGenderPreference(this.genderPreference);
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
        groupDto.groupId = groups.getGroupId();
        groupDto.introduce = groups.getIntroduce();
        groupDto.description = groups.getDescription();
        groupDto.category = groups.getCategory().getGroupCategoryId();
        groupDto.categoryName = groups.getCategory().getCategory();
        groupDto.location = groups.getLocation();
        groupDto.genderPreference = groups.getGenderPreference();
        groupDto.registrationType = groups.getRegistrationType();
        groupDto.currentHeadCount = groups.getCurrentHeadCount();
        groupDto.capacity = groups.getCapacity();
        groupDto.profileImgUrl = groups.getProfileImgUrl();
        return groupDto;
    }

    public GroupDto(Groups group, List<GroupMember> groupMembers) {
        this.name = group.getName();
        this.introduce = group.getIntroduce();
        this.category = group.getCategory().getGroupCategoryId();
        this.categoryName = group.getCategory().getCategory();
        this.location = group.getLocation();
        this.genderPreference = group.getGenderPreference();
        this.currentHeadCount = group.getCurrentHeadCount();
        this.registrationType = group.getRegistrationType();
        this.capacity = group.getCapacity();
        this.profileImgUrl = group.getProfileImgUrl();
        this.groupId = group.getGroupId();
        this.description = group.getDescription();
        this.createdBy = group.getCreatedBy().getId();//모임장 위임관련
        this.members = groupMembers.stream()
                .map(GroupMember::getUser)
                .map(UserDto::of)
                .collect(Collectors.toList());
    }

}
