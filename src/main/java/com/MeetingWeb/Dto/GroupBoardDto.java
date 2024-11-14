package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.GroupBoard;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class GroupBoardDto {

    private Long boardId;
    private String title;
    private String content;
    private String userName;  // 작성자 이름
    private Long groupId;
    private Long userId;
    private String profileImgUrl;  // 프로필 이미지 경로 추가



    public static GroupBoard toEntity(GroupBoardDto dto, Groups group, User user) {
        GroupBoard groupBoard = new GroupBoard();
        groupBoard.setTitle(dto.getTitle());
        groupBoard.setContent(dto.getContent());
        groupBoard.setGroup(group); // Groups 엔티티 설정
        groupBoard.setUser(user);   // User 엔티티 설정
        return groupBoard;
    }

    public static GroupBoardDto of(GroupBoard groupBoard) {
        GroupBoardDto dto = new GroupBoardDto();
        dto.setGroupId(groupBoard.getGroup().getGroupId());
        dto.setBoardId(groupBoard.getBoardId());
        dto.setTitle(groupBoard.getTitle());
        dto.setContent(groupBoard.getContent());  // content 필드에 이미지와 내용이 포함됨
        dto.setUserName(groupBoard.getUser().getName());
        dto.setProfileImgUrl(groupBoard.getUser().getProfileImgUrl());  // 프로필 이미지



        return dto;
    }

}
