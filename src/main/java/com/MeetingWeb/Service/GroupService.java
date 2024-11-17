package com.MeetingWeb.Service;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Constant.RegistType;
import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Dto.*;
//import com.MeetingWeb.Entity.GroupDescriptionImg;
import com.MeetingWeb.Entity.*;
//import com.MeetingWeb.Repository.GroupDescriptionRepository;
import com.MeetingWeb.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    //    private final GroupDescriptionRepository groupDescriptionRepository;
    private final ProfileUploadService profileUploadService;
    private final GroupCategoryRepository groupCategoryRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupApplicationRepository groupApplicationRepository;
    private final TournamentRepository tournamentRepository;
    private final GroupBoardRepository groupBoardRepository;
    private final DataSourceTransactionManagerAutoConfiguration dataSourceTransactionManagerAutoConfiguration;

    @Transactional
    public GroupDto createGroup(GroupDto groupDto, User createdBy) throws IOException {
        String profileImageUrl = profileUploadService.saveProfile(groupDto.getProfileImg());

        GroupCategory groupCategory = groupCategoryRepository.findById(groupDto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + groupDto.getCategory()));

        Groups group = groupDto.toEntity(profileImageUrl, createdBy, groupCategory);
        if(!createdBy.getRole().equals(Role.ADMIN)) {
            createdBy.setRole(Role.LEADER);
        }
        group.setCurrentHeadCount(1);
        groupRepository.save(group);
        GroupMember groupMember = new GroupMember();
        groupMember.setGroup(group);
        groupMember.setUser(createdBy);
        groupMemberRepository.save(groupMember);


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

    public List<GroupDto> getPopularGroup() {
        List<GroupDto> popularGroup = groupRepository.findAllByOrderByCurrentHeadCountDesc().stream()
                .map(GroupDto::of)
                .collect(Collectors.toList());

        return popularGroup;
    }


    public GroupDto findGroupById(Long id) { //목록상세
        Groups group = groupRepository.findByGroupId(id);
        if (group != null) {//객체안에 값 존재여부//있다면 안에 내용 실행
            List<GroupMember> groupMembers = groupMemberRepository.findByGroup(group);//그룹멤버에 해당모임이 있는지 확인
            GroupDto dto = new GroupDto(group, groupMembers);
            return dto;
        } else {//없다면 null
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
            if (groupMemberRepository.existsByGroupAndUser(group, user)) {//중복가입방지
                return false;
            }
            return addMemberToGroup(groupId, user);
        }
        return false;
    }

    //승인가입
    public boolean approve(Long groupId, String userName, GroupApplicationDto groupApplicationDto) {
        Groups group = groupRepository.findById(groupId).orElse(null);
        User user = userRepository.findByUserName(userName);
        if (group != null && user != null && group.getRegistrationType() == RegistType.APPROVAL) {
            if (groupApplicationRepository.existsByGroupAndUser(group, user)) {
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

    public GroupProfileDto getGroupProfile(Long groupId) {
        Groups group = groupRepository.findByGroupId(groupId);
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
    public List<GroupDto> getMyParticipatingGroup(String userName, List<GroupDto> myGroup) {
        User user = userRepository.findByUserName(userName);

        List<Groups> myParticipatingGroup = groupRepository.findMyGroups(user);

        Iterator<Groups> it = myParticipatingGroup.iterator();

        while(it.hasNext()){
            Groups myPtGroup = it.next();
            for(int k=0; k<myGroup.size(); k++){
                if(myPtGroup.getGroupId() == myGroup.get(k).getGroupId()){
                    it.remove();
                }
            }
        }

        if (myParticipatingGroup.isEmpty()) {
            return Collections.emptyList(); // 내가 가입한 모임의 대회가 없는 경우 빈 리스트 반환
        }

        return myParticipatingGroup.stream()
                .map(GroupDto::of)
                .collect(Collectors.toList());
    }

    public void deleteGroup(Long id){
        if (groupRepository.existsById(id)) {
            groupRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("해당 ID를 가진 그룹이 존재하지 않습니다.");
        }
    }

    public boolean isGroupOwner(Long userId, Long groupId) {//모임만든사람 찾기
        Groups group = groupRepository.findByGroupId(groupId);
        Long ownerId = group.getCreatedBy().getId();
        if (ownerId.equals(userId)) {
            return true;
        }
        return false;
    }

    public boolean isMemberOfGroup(Long userId, Long groupId) {//모임회원 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));
        // 사용자와 그룹을 통해 회원 여부를 확인
        return groupMemberRepository.existsByUserAndGroup(user, group);
    }

    public List<GroupBoardDto> findPostsByGroupId(Long groupId) { //모임게시판 목록
        List<GroupBoard> groupBoardList = groupBoardRepository.findAllByGroup_GroupId(groupId);
        List<GroupBoardDto> groupBoardDtoList = new ArrayList<>();
        for (GroupBoard groupBoard : groupBoardList) {
            groupBoardDtoList.add(GroupBoardDto.of(groupBoard));
        }
        return groupBoardDtoList;

    }

    @Transactional
    public void groupBoardSave(GroupBoardDto groupBoardDto) {//모임게시글 저장
        Groups group = groupRepository.findById(groupBoardDto.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid group ID"));
        User user = userRepository.findById(groupBoardDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        GroupBoard groupBoard = GroupBoardDto.toEntity(groupBoardDto, group, user);
        groupBoardRepository.save(groupBoard);
    }

    public GroupBoardDto findBoardById(Long id) {
        Optional<GroupBoard> optionalGroupBoard = groupBoardRepository.findById(id);
        if (optionalGroupBoard.isPresent()) {
            GroupBoard groupBoard = optionalGroupBoard.get();
            GroupBoardDto groupBoardDto = GroupBoardDto.of(groupBoard);
            return groupBoardDto;
        } else {
            return null;
        }
    }

    public GroupBoardDto updateBoard(Long boardId, GroupBoardDto groupBoardDto) {
        // postId로 기존 게시글을 조회하고 업데이트
        GroupBoard groupBoard = groupBoardRepository.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        // DTO의 데이터를 기존 엔티티에 설정
        groupBoard.setTitle(groupBoardDto.getTitle());
        groupBoard.setContent(groupBoardDto.getContent());

        // 수정된 엔티티를 저장
        groupBoardRepository.save(groupBoard);

        // 업데이트된 내용을 DTO로 변환하여 반환
        return GroupBoardDto.of(groupBoard);
    }


    //회원강퇴
    @Transactional
    public void removeMember(Long groupId, Long userId) {
        GroupMember member = groupMemberRepository.findByGroup_GroupIdAndUser_Id(groupId, userId)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        groupMemberRepository.delete(member);
    }

    //모임장 위임
    public void delegateGroup(Long groupId, Long userId) {
        Groups group = groupRepository.findByGroupId(groupId);
        User currentGroupLeader = group.getCreatedBy();
        User postGroupLeader = userRepository.findById(userId).get();

        group.setCreatedBy(postGroupLeader);
        currentGroupLeader.setRole(Role.USER);
        postGroupLeader.setRole(Role.LEADER);

        groupRepository.save(group);
        userRepository.save(currentGroupLeader);
        userRepository.save(postGroupLeader);
    }


    private boolean addMemberToGroup(Long groupId, User user) {//모임가입시 인원증가 및 정원초과 방지
        Groups group = groupRepository.findById(groupId).orElseThrow(() -> new RuntimeException("Group not found"));
        int groupMemCount = groupMemberRepository.countByGroup(group);

        if (group.getCurrentHeadCount() < group.getCapacity()) {
            group.setCurrentHeadCount(groupMemCount + 1);
            groupRepository.save(group);

            GroupMember groupMember = new GroupMember();
            groupMember.setGroup(group);
            groupMember.setUser(user);

            groupMemberRepository.save(groupMember);

            return true;
            // 멤버 추가 로직
        } else {
//            throw new RuntimeException("Capacity has been reached.");
            return false;
        }

    }

    //신청서목록보여주기
    public List<GroupApplicationDto> getApplicationId(Long groupId) {
        List<GroupApplication>applicationList = groupApplicationRepository.findByGroup_GroupId(groupId);
        List<GroupApplicationDto> groupApplicationDtoList = new ArrayList<>();
        for (GroupApplication groupApplication : applicationList) {
            GroupApplicationDto groupApplicationDto = GroupApplicationDto.of(groupApplication);
            groupApplicationDtoList.add(groupApplicationDto);
        }
        return groupApplicationDtoList;

    }

    //승인가입 수락 후 모임가입저장
    public void acceptApplication(Long groupId, Long userId) {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        GroupApplication groupApplication = groupApplicationRepository.findByUser_Id(userId);

        // 그룹의 현재 인원 수 증가
        addMemberToGroup(groupId, user);
        groupApplicationRepository.deleteById(groupApplication.getApplicationId());


    }

    //활동피드삭제
    public void deletePost(Long boardId) {
        groupBoardRepository.deleteById(boardId); // postId로 게시글 삭제
    }


    //활동피드권한 (작성자,모임장만 가능)
    public boolean canDeletePost(Long userId, Long groupId, Long boardId) {
        // 게시글 정보를 가져옴
        GroupBoardDto post = findBoardById(boardId);
        if (post == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글입니다.");
        }

        // 작성자인지 확인 지금작성자 확인이 안되는중
          boolean isAuthor =userId.equals(post.getUserId());
//        User user=userRepository.findByUserName(userId);
//        boolean isAuthor=user.getUserName().equals(post.getUserName());
        System.out.println("ssssssssssssssssss"+isAuthor);
        // 모임장인지 확인
          boolean isGroupOwner = isGroupOwner(userId, groupId);

        return isAuthor || isGroupOwner;
    }
    //모임장탈퇴
    @Transactional
    public void groupLeave(Long groupId, Long userId) {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 모임장 여부 확인
        if (isGroupOwner(userId, groupId)) {
            // 다음 모임장 찾기
            GroupMember nextOwner = groupMemberRepository.findFirstByGroup_GroupIdOrderByJoinedAtAsc(groupId)
                    .orElseThrow(() -> new IllegalStateException("No members available to take ownership"));

            group.setCreatedBy(nextOwner.getUser()); // 다음 모임장을 설정
            groupRepository.save(group);
        }

        // 현재 회원 삭제
        GroupMember currentGroupOwner = groupMemberRepository.findByGroupAndUser(group, user);

        groupMemberRepository.delete(currentGroupOwner);

        // 모임 인원 감소
        group.setCurrentHeadCount(group.getCurrentHeadCount() - 1);
        groupRepository.save(group);
    }

    //일반회원탈퇴
    @Transactional
    public void memberLeave(Long groupId, Long userId) {
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 현재 회원 삭제
        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user);

        groupMemberRepository.delete(member);

        // 모임 인원 감소
        group.setCurrentHeadCount(group.getCurrentHeadCount() - 1);
        groupRepository.save(group);

    }



}














