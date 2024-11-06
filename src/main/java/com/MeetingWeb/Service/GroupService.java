package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.GroupDto;
//import com.MeetingWeb.Entity.GroupDescriptionImg;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
//import com.MeetingWeb.Repository.GroupDescriptionRepository;
import com.MeetingWeb.Repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
//    private final GroupDescriptionRepository groupDescriptionRepository;
    private final ProfileUploadService profileUploadService;

    @Transactional
    public GroupDto createGroup(GroupDto groupDto, User createdBy) throws IOException {
        String profileImageUrl = profileUploadService.saveProfile(groupDto.getProfileImg());
        Groups group = groupDto.toEntity(profileImageUrl,createdBy);
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
}
