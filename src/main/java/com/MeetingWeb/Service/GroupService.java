package com.MeetingWeb.Service;

import com.MeetingWeb.Constant.RegistType;
import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Dto.GroupApplicationDto;
import com.MeetingWeb.Dto.GroupDto;
//import com.MeetingWeb.Entity.GroupDescriptionImg;
import com.MeetingWeb.Dto.GroupProfileDto;
import com.MeetingWeb.Entity.*;
//import com.MeetingWeb.Repository.GroupDescriptionRepository;
import com.MeetingWeb.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
//    private final GroupDescriptionRepository groupDescriptionRepository;
    private final ProfileUploadService profileUploadService;
    private final GroupCategoryRepository groupCategoryRepository;
    private final  UserService userService;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupApplicationRepository groupApplicationRepository;

    @Transactional
    public GroupDto createGroup(GroupDto groupDto, User createdBy) throws IOException {
        String profileImageUrl = profileUploadService.saveProfile(groupDto.getProfileImg());

        GroupCategory groupCategory = groupCategoryRepository.findById(groupDto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + groupDto.getCategory()));

        Groups group = groupDto.toEntity(profileImageUrl,createdBy, groupCategory);
        createdBy.setRole(Role.READER);
        groupRepository.save(group);


//        if (groupDto.getDescriptionImageUrls() != null) {
//            for (String url : groupDto.getDescriptionImageUrls()) {
//                GroupDescriptionImg img = new GroupDescriptionImg();
//                img.setImgUrl(url);
//                img.setGroup(group);
//                groupDescriptionRepository.save(img);
//            }
//        }
        return groupDto;
    }

    public GroupDto getGroupById(Long id) {
        Groups groups = groupRepository.findByGroupId(id);
        return GroupDto.of(groups);
    }
    public List<GroupDto> getAllGroups() {
        // 전체 그룹을 가져오고 GroupDto로 변환하면서 카테고리 이름을 설정
        return groupRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(group -> {
                    GroupDto dto = GroupDto.of(group);
//                    if (group.getCategory() != null) {
//                        dto.setCategoryName(group.getCategory().getCategory()); // 카테고리 이름 설정
//                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    public GroupDto findGroupById(Long id) { //목록상세
        Groups group = groupRepository.findByGroupId(id);
        if(group!=null) {//객체안에 값 존재여부//있다면 안에 내용 실행
            GroupDto dto = GroupDto.of(group);
            return dto;
        }else {//없다면 null
            return null;
        }
    }
    public List<GroupDto> getCustomGroupsForUser(String userName) {
        // 유저가 선택한 카테고리를 가져오고, 모임 카테고리와 일치하는 모임들을 찾은 후 GroupDto 형식의 리스트로 변환
        List<GroupCategory> selectedCategories = userService.getUserSelectedCategories(userName);
        return groupRepository.findByCategoryIn(selectedCategories)
                .stream()
                .map(GroupDto::of)
                .collect(Collectors.toList());
    }
    public boolean joinGroup(Long groupId, String userName) {//자유가입
        Groups group = groupRepository.findById(groupId).orElse(null);
        User user = userRepository.findByUserName(userName);

        if (group != null && user != null && group.getRegistrationType() == RegistType.FREE) {
            if(groupMemberRepository.existsByGroupAndUser(group, user)) {//중복가입방지

                return false;
            }
            GroupMember groupMember = new GroupMember();
            groupMember.setUser(user);
            groupMember.setGroup(group);

            groupMemberRepository.save(groupMember);  // GroupMember 저장

            return true;
        }
        return false;
    }
    //승인가입
    public boolean approve(Long groupId, String userName, GroupApplicationDto groupApplicationDto) {
        Groups group = groupRepository.findById(groupId).orElse(null);
        User user = userRepository.findByUserName(userName);
        if (group != null && user != null && group.getRegistrationType() == RegistType.APPROVAL) {
            if(groupApplicationRepository.existsByGroupAndUser(group, user)) {
                return false;
            }
            GroupApplication groupApplication = new GroupApplication();
            groupApplication.setUser(user);
            groupApplication.setGroup(group);
            groupApplication.setSay(groupApplicationDto.getSay());
            groupApplication.setReason(groupApplicationDto.getReason());



            groupApplicationRepository.save(groupApplication);


            return true;
        }
        return false;
    }

    public GroupProfileDto getGroupProfile(Long createdBy) {
        Groups group = groupRepository.findByCreatedById(createdBy)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹을 찾을 수 없습니다. groupId: " + createdBy));
        return GroupProfileDto.of(group);
    }
}
