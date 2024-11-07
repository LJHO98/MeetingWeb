package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.GroupDto;
//import com.MeetingWeb.Entity.GroupDescriptionImg;
import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.TournamentCategory;
import com.MeetingWeb.Entity.User;
//import com.MeetingWeb.Repository.GroupDescriptionRepository;
import com.MeetingWeb.Repository.GroupCategoryRepository;
import com.MeetingWeb.Repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    @Transactional
    public GroupDto createGroup(GroupDto groupDto, User createdBy) throws IOException {
        String profileImageUrl = profileUploadService.saveProfile(groupDto.getProfileImg());

        GroupCategory groupCategory = groupCategoryRepository.findById(groupDto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + groupDto.getCategory()));

        Groups group = groupDto.toEntity(profileImageUrl,createdBy, groupCategory);
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
}
