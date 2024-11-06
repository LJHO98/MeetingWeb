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
        return groupRepository.findAll().stream()
                .map(group -> {
                    GroupDto dto = GroupDto.of(group);
//                    if (group.getCategory() != null) {
//                        dto.setCategoryName(group.getCategory().getCategory()); // 카테고리 이름 설정
//                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
