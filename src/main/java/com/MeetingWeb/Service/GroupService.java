package com.MeetingWeb.Service;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.RegistType;
import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Dto.GroupApplicationDto;
import com.MeetingWeb.Dto.GroupDto;
//import com.MeetingWeb.Entity.GroupDescriptionImg;
import com.MeetingWeb.Dto.GroupProfileDto;
import com.MeetingWeb.Dto.TrnDto;
import com.MeetingWeb.Entity.*;
//import com.MeetingWeb.Repository.GroupDescriptionRepository;
import com.MeetingWeb.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Collections;
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
    private final TournamentRepository tournamentRepository;

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

    public List<GroupDto> getFilteredGroups(String keyword, String gender, Long category, String location) {
        // 1. 모든 그룹을 최신순으로 가져옵니다.
        List<Groups> groups = groupRepository.findAllByOrderByCreatedAtDesc();

        // 2. 키워드 필터링 (keyword가 null이 아니고 비어 있지 않을 때)
        if (keyword != null && !keyword.trim().isEmpty()) {
            String lowerKeyword = keyword.toLowerCase();
            groups = groups.stream()
                    .filter(group -> group.getName().toLowerCase().contains(lowerKeyword) ||
                            group.getIntroduce().toLowerCase().contains(lowerKeyword))
                    .collect(Collectors.toList());
        }

        // 3. 성별 필터링 (gender 값이 "all"이 아닐 때)
        if (gender != null && !gender.equalsIgnoreCase("all")) {
            try {
                Gender genderFilter = Gender.valueOf(gender.toUpperCase());
                groups = groups.stream()
                        .filter(group -> group.getGenderPreference() == genderFilter)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException e) {
                // 잘못된 gender 값이 들어온 경우 무시하고 전체를 반환
            }
        }

        // 4. 카테고리 필터링 (category 값이 null이 아닐 때)
        if (category != null) {
            groups = groups.stream()
                    .filter(group -> group.getCategory().getGroupCategoryId().equals(category))
                    .collect(Collectors.toList());
        }

        // 5. 지역 필터링 (location 값이 null이 아닐 때)
        if (location != null && !location.trim().isEmpty()) {
            String lowerLocation = location.toLowerCase();
            groups = groups.stream()
                    .filter(group -> group.getLocation().toLowerCase().contains(lowerLocation))  // 지역 비교
                    .collect(Collectors.toList());
        }

        // 6. 필터링된 그룹 목록을 DTO 형식으로 변환하여 반환
        return groups.stream().map(GroupDto::of).collect(Collectors.toList());
    }

    //내가 만든 모임
    public List<GroupDto> getMyGroup(String userName) {
        User user = userRepository.findByUserName(userName);
        List<Groups> groups = groupRepository.findByCreatedBy(user);

        if (groups.isEmpty()) {
            return Collections.emptyList(); // 모임이 없는 경우 빈 리스트 반환
        }

        return groups.stream()
                .map(GroupDto::of)
                .collect(Collectors.toList());
    }

    //내가 가입한 모임
    public List<GroupDto> getMyParticipatingGroup(String userName) {
        User user = userRepository.findByUserName(userName);

        List<Groups> myParticipatingGroup = groupRepository.findMyGroups(user);

        if (myParticipatingGroup.isEmpty()) {
            return Collections.emptyList(); // 내가 가입한 모임의 대회가 없는 경우 빈 리스트 반환
        }

        return myParticipatingGroup.stream()
                .map(GroupDto::of)
                .collect(Collectors.toList());
    }

}
