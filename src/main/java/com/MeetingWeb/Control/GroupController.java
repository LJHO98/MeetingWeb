package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.*;
import com.MeetingWeb.Entity.GroupMember;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.UserRepository;
import com.MeetingWeb.Service.EventsService;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.TournamentService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private final TournamentService tournamentService;
    private final EventsService eventsService;

    //모임 만들기
    @PostMapping("/group/createGroup")
    public String createGroup(@Valid GroupDto groupDto, BindingResult bindingResult, Model model,
                              @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", userService.getGroupCategories());
            return "group/createGroup";
        }

        String username = userDetails.getUsername();
        User createdBy = userService.findByUserName(username);
        groupService.createGroup(groupDto, createdBy);
        return "home";
    }

    //모임 생성폼
    @GetMapping("/group/createGroup")
    public String showCreateForm(Model model) {
        model.addAttribute("categories", userService.getGroupCategories());
        model.addAttribute("groupDto", new GroupDto());
        return "group/createGroup";
    }

    //모임 정보 수정
    @GetMapping("/group/edit/{groupId}")
    public String updateGroup(@PathVariable Long groupId, Model model) {
        GroupDto groupDto = groupService.getGroupById(groupId);
        model.addAttribute("categories", userService.getGroupCategories());
        model.addAttribute("groupDto", groupDto);

        return "group/createGroup";

    }

    //모임 목록
    @GetMapping("/group/list")
    public String groupList(Model model,
                            @RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String gender,
                            @RequestParam(required = false) Long categoryId,
                            @RequestParam(required = false) String location) {

        // 카테고리 목록 조회 및 그룹 필터링
        List<GroupCategoryDto> categories = userService.getGroupCategories();
        List<GroupDto> groupDtoList = groupService.getFilteredGroups(keyword, gender, categoryId, location);

        model.addAttribute("categories", categories);
        model.addAttribute("groupList", groupDtoList);

        return "group/groupList";
    }

    //모임 상세 페이지
    @GetMapping("/group/{id}")
    public String groupDetail(@PathVariable Long id, Model model,@AuthenticationPrincipal UserDetails userDetails) {
        // 그룹 정보를 가져옴
        GroupDto groupDto = groupService.findGroupById(id);
        model.addAttribute("groupDetail", groupDto);

        // 현재 로그인한 사용자가 모임 생성자인지 확인
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        boolean isGroupOwner = groupService.isGroupOwner(userId, id);
        model.addAttribute("isGroupOwner", isGroupOwner); // 결과를 모델에 추가

        // 현재 로그인한 사용자가 그룹에 가입되어있는지확인하고 탈퇴버튼 생성
        boolean isMember=groupService.isMemberOfGroup(userId, id);
        model.addAttribute("isMember", isMember);

        List<EventsDto> events = eventsService.getEventsByGroupId(id);
        model.addAttribute("events", events);

        return "group/groupInfo";
    }

    //모임 자유 가입
    @PostMapping("/group/join/{groupId}")
    public String joinGroup(@PathVariable Long groupId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        boolean isJoined = groupService.joinGroup(groupId, username);

        String message = isJoined ? "모임에 성공적으로 가입되었습니다." : "이미 가입한 모임입니다.";
        try {
            message = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:/group/" + groupId + "?message=" + message;
    }

    //모임 가입 신청
    @PostMapping("/group/approval/{groupId}")
    public String approveGroup(@PathVariable Long groupId,
                               @AuthenticationPrincipal UserDetails userDetails,
                               @ModelAttribute GroupApplicationDto groupApplicationDto) {
        String username = userDetails.getUsername();
        boolean isJoined = groupService.approve(groupId, username, groupApplicationDto);

        String message = isJoined ? "신청이 완료되었습니다." : "이미 신청한 모임입니다.";
        try {
            message = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:/group/" + groupId + "?message=" + message;
    }

    //내 모임
    @GetMapping("/group/myGroup")
    public String myGroupPage(Principal principal, Model model) {
        List<GroupDto> myGroup = groupService.getMyGroup(principal.getName());
        List<GroupDto> myParticipatingGroup = groupService.getMyParticipatingGroup(principal.getName(), myGroup);

        if (myGroup.isEmpty()) {
            model.addAttribute("myGroupMessage", "내가 만든 모임이 없습니다.");
        } else {
            model.addAttribute("myGroup", myGroup);
        }

        if (myParticipatingGroup.isEmpty()) {
            model.addAttribute("myParticipatingGroupMessage", "내가 가입한 모임이 없습니다.");
        } else {
            model.addAttribute("myParticipatingGroup", myParticipatingGroup);
        }

        return "group/myGroup";
    }

    //내 모임 참가대회
    @GetMapping("/group/{groupId}/tournament")
    public String GroupTournamentPage(@PathVariable("groupId") Long groupId , Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        List<TrnDto> groupTournament = tournamentService.getGroupTournament(groupId);
        GroupDto groupDto = groupService.getGroupById(groupId);
        model.addAttribute("groupDetail", groupDto);
        model.addAttribute("groupTournament", groupTournament);
        model.addAttribute("isGroupOwner", groupService.isGroupOwner(userId, groupId));
        model.addAttribute("isMember", groupService.isMemberOfGroup(userId, groupId));
        return "group/groupTournament";

    }
    //모임 게시판 페이지
    @GetMapping("/group/{groupId}/groupBoard")
    public String groupBoard(@PathVariable Long groupId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        GroupDto groupDto = groupService.findGroupById(groupId); // 그룹 정보를 가져옵니다.
        // 특정 그룹과 게시판의 게시글 리스트를 가져옵니다.
        List<GroupBoardDto> posts = groupService.findPostsByGroupId(groupId);
        model.addAttribute("groupDetail", groupDto); // 그룹 정보를 모델에 추가합니다.
        model.addAttribute("posts", posts); // 게시글 리스트를 모델에 추가합니다.
        model.addAttribute("isGroupOwner", groupService.isGroupOwner(userId, groupId));
        model.addAttribute("isMember", groupService.isMemberOfGroup(userId, groupId));

        return "group/groupBoard"; // groupBoard 템플릿으로 이동합니다.
    }
    // 게시글 작성 페이지 이동
    @GetMapping("/group/{id}/write")
    public String groupBoard(Model model, @PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        model.addAttribute("groupBoard", new GroupBoardDto());
        GroupDto groupDto = groupService.findGroupById(id); // 여기서 id 사용
        model.addAttribute("groupDetail", groupDto);
        model.addAttribute("isGroupOwner", groupService.isGroupOwner(userId, id));
        model.addAttribute("isMember", groupService.isMemberOfGroup(userId, id));
        return "group/writePage";
    }


    //게시글 저장
    @PostMapping("/group/{groupId}/write")
    public String saveBoard(@ModelAttribute GroupBoardDto groupBoardDto, @PathVariable Long groupId, RedirectAttributes redirectAttributes) {
        Long userId = userService.getLoggedUserId(); // 로그인된 사용자 ID 가져오기
        if (!groupService.isMemberOfGroup(userId, groupId)) {//해당그룹회원이 아닐시
            redirectAttributes.addFlashAttribute("errorMessage", "모임 회원이 아니므로 작성할 수 없습니다.");
            return "redirect:/group/" + groupId; // 모임 상세 페이지로 리다이렉트
        }//맞다면 저장
        groupBoardDto.setGroupId(groupId);
        groupBoardDto.setUserId(userId);

        groupService.groupBoardSave(groupBoardDto);
        return "redirect:/group/" + groupId +"/groupBoard";
    }
    //활동피드삭제
    @DeleteMapping("/group/post/{boardId}")
    public ResponseEntity<String> deletePost(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();

        boolean isDeleted = groupService.deletePost(boardId, userId);
        if (isDeleted) {
            return ResponseEntity.ok("게시글이 삭제되었습니다.");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
    }


    @GetMapping("/group/{groupId}/updateWrite/{boardId}")
    public String updateForm(@PathVariable Long groupId, @PathVariable Long boardId, Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // boardId를 사용하여 게시글 조회
        GroupBoardDto groupBoardDto = groupService.findBoardById(boardId);

        // 사용자 ID를 가져옴
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();

        // groupId를 사용하여 그룹 정보 조회
        GroupDto groupDto = groupService.findGroupById(groupId);

        // 모델에 그룹 정보와 게시글 정보 추가
        model.addAttribute("groupDetail", groupDto);
        model.addAttribute("groupBoard", groupBoardDto); // 기존 게시글 정보 추가
        model.addAttribute("isGroupOwner", groupService.isGroupOwner(userId, groupId));
        model.addAttribute("isMember", groupService.isMemberOfGroup(userId, groupId));

        // updateWrite 템플릿 반환
        return "group/updateWrite";
    }


    //모임 게시글 수정저장
    @PostMapping("/group/{groupId}/updateWrite/{boardId}")
    public String updateSave(
            @ModelAttribute GroupBoardDto groupBoardDto,
            @PathVariable Long groupId,
            @PathVariable Long boardId,
            RedirectAttributes redirectAttributes) {

        Long userId = userService.getLoggedUserId(); // 로그인된 사용자의 ID 가져오기

        // 게시글 작성자 또는 모임장인지 확인하여 수정 권한 체크
        if (!groupService.canDeletePost(userId, groupId, boardId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "수정 권한이 없습니다.");
            return "redirect:/group/" + groupId + "/groupBoard"; // 수정 불가 시 게시글 목록 페이지로 리다이렉트
        }

        // 그룹 회원인지 확인
        if (!groupService.isMemberOfGroup(userId, groupId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "모임 회원이 아니므로 수정할 수 없습니다.");
            return "redirect:/group/" + groupId; // 모임 상세 페이지로 리다이렉트
        }

        // 게시글 업데이트 수행
        groupBoardDto.setGroupId(groupId);
        groupBoardDto.setBoardId(boardId);

        // 게시글 업데이트 수행
        GroupBoardDto updatedGroupBoard = groupService.updateBoard(boardId, groupBoardDto);

        // 성공 메시지를 Flash Attribute로 추가
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");
        return "redirect:/group/" + groupId + "/groupBoard"; // 수정 완료 후 게시글 목록 페이지로 리다이렉트
    }


    //활동피드삭제
    @DeleteMapping("/group/{groupId}/groupBoard/{boardId}")
    public ResponseEntity<String> deletePost(@PathVariable Long groupId,
                                             @PathVariable Long boardId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        if (!groupService.canDeletePost(userId, groupId, boardId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }
        // 권한이 있으면 삭제 수행
        groupService.deletePost(boardId);
        return ResponseEntity.ok("게시글이 성공적으로 삭제되었습니다.");
    }



    //모임 관리 페이지
    @GetMapping("/group/{groupId}/groupAdmin")
    public String groupAdmin(@PathVariable Long groupId, Model model) {
        GroupDto groupDto = groupService.findGroupById(groupId); // 그룹 정보를 가져옵니다.
        model.addAttribute("groupDetail", groupDto); // 그룹 정보를 모델에 추가합니다.
        return "/groupAdmin/groupAdmin";
    }

    //회원관리페이지
    @GetMapping("/group/{groupId}/memberAdmin")
    public String memberAdmin(@PathVariable Long groupId, Model model,@AuthenticationPrincipal UserDetails userDetails) {

        GroupDto groupDto = groupService.findGroupById(groupId);
        model.addAttribute("groupOwner", groupDto.getCreatedBy());
        model.addAttribute("groupDetail", groupDto);
        return "groupAdmin/memberAdmin";
    }

    //회원강퇴
    @DeleteMapping("/group/{groupId}/removeMember/{userId}")
    public ResponseEntity<String> removeMember(@PathVariable Long groupId, @PathVariable Long userId) {
        groupService.removeMember(groupId, userId); // 회원을 강퇴하는 서비스 메서드 호출
        return ResponseEntity.ok("회원이 강퇴되었습니다.");
    }

    //모임장 위임
    @PostMapping("/group/delegate")
    public ResponseEntity<String> delegateGroup(@RequestParam Long groupId, @RequestParam Long userId) {
        groupService.delegateGroup(groupId, userId);
        return ResponseEntity.ok("모임장 변경 완료.");

    }
    //신청서목록
    @GetMapping("/group/{groupId}/application")
    public String applicationAdmin(@PathVariable Long groupId,Model model) {
        GroupDto groupDto = groupService.findGroupById(groupId);
        List<GroupApplicationDto> applicationDtoList=groupService.getApplicationId(groupId);
        model.addAttribute("groupDetail", groupDto);
        model.addAttribute("applicationDtoList", applicationDtoList);
        return "groupAdmin/applicationAdmin";
    }
    //신청수락 후 해당모임 가입저장
    @PostMapping("/group/{groupId}/application/{userId}")
    public ResponseEntity<String> acceptApplication(@PathVariable Long groupId, @PathVariable Long userId, Model model) {
        groupService.acceptApplication(groupId, userId);
        return ResponseEntity.ok("신청 수락 완료");
    }
    //신청거절 데이터삭제
    @PostMapping("/group/{groupId}/application/{userId}/reject")
    public ResponseEntity<String> acceptApplicationRefusal(@PathVariable Long groupId, @PathVariable Long userId, Model model) {
        groupService.refusalApplication(groupId, userId);
        return ResponseEntity.ok("신청 거절 완료");
    }

    //모임장 탈퇴시 가입오래된 순서로 자동위임
    @PostMapping("/group/{groupId}/leave")
    public String groupLeave(@PathVariable Long groupId, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        groupService.groupLeave(groupId, userId);
        return "redirect:/group/" + groupId;
    }
    //일반회원탈퇴
    @PostMapping("/group/leave/{groupId}")
    public String leaveGroup(@PathVariable Long groupId, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        groupService.memberLeave(groupId, userId);
        return "redirect:/group/" + groupId;
    }
    //일정관리페이지 이동(일정리스트도 같이 보여줌)
    @GetMapping("/groupAdmin/{groupId}/eventAdmin")
    public String eventAdmin(@PathVariable Long groupId, Model model) {
        GroupDto groupDto = groupService.findGroupById(groupId);
        model.addAttribute("eventsDto", new EventsDto());
        model.addAttribute("groupDetail", groupDto);
        List<EventsDto> events = eventsService.getEventsByGroupId(groupId);
        model.addAttribute("events", events); // 조회된 일정을 모델에 추가
        return "groupAdmin/eventAdmin";
    }

}
