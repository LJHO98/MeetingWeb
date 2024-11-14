package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.*;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.UserRepository;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.TournamentService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
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
    private final UserRepository userRepository;

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

    @GetMapping("/group/createGroup")
    public String showCreateForm(Model model) {
        model.addAttribute("categories", userService.getGroupCategories());
        model.addAttribute("groupDto", new GroupDto());
        return "group/createGroup";
    }

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

    @GetMapping("/group/{id}")
    public String groupDetail(@PathVariable Long id, Model model,@AuthenticationPrincipal UserDetails userDetails) {
        // 그룹 정보를 가져옴
        GroupDto groupDto = groupService.findGroupById(id);
        model.addAttribute("groupDetail", groupDto);

        // 현재 로그인한 사용자가 모임 생성자인지 확인
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        boolean isGroupOwner = groupService.isGroupOwner(userId, id);
        model.addAttribute("isGroupOwner", isGroupOwner); // 결과를 모델에 추가

        return "group/groupInfo";
    }

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
    public String GroupTournamentPage(@PathVariable("groupId") Long groupId , Model model) {
        List<TrnDto> groupTournament = tournamentService.getGroupTournament(groupId);
        GroupDto groupDto = groupService.getGroupById(groupId);
        model.addAttribute("groupDetail", groupDto);
        model.addAttribute("groupTournament", groupTournament);
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

        return "group/groupBoard"; // groupBoard 템플릿으로 이동합니다.
    }

    @GetMapping("/group/{id}/write")//게시글작성페이지 이동
    public String groupBoard(Model model, @PathVariable Long id) {
        model.addAttribute("groupBoard", new GroupBoardDto());
        return "group/writePage";
    }
    // 로그인된 사용자 ID를 가져오는 메서드
    private Long getLoggedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUserName(userDetails.getUsername());
            return user.getId();
        }
        throw new IllegalStateException("로그인된 사용자가 없습니다.");
    }

    @PostMapping("/group/{groupId}/write")//게시글 저장
    public String saveBoard(@ModelAttribute GroupBoardDto groupBoardDto, @PathVariable Long groupId, RedirectAttributes redirectAttributes) {
        Long userId = getLoggedUserId(); // 로그인된 사용자 ID 가져오기
        if (!groupService.isMemberOfGroup(userId, groupId)) {//해당그룹회원이 아닐시
            redirectAttributes.addFlashAttribute("errorMessage", "모임 회원이 아니므로 작성할 수 없습니다.");
            return "redirect:/group/" + groupId; // 모임 상세 페이지로 리다이렉트
        }//맞다면 저장
        groupBoardDto.setGroupId(groupId);
        groupBoardDto.setUserId(userId);

        groupService.groupBoardSave(groupBoardDto);
        return "redirect:/group/" + groupId +"/groupBoard";
    }

    //모임 게시글 수정페이지
    @GetMapping("/group/{groupId}/updateWrite/{boardId}")
    public String updateForm(@PathVariable Long groupId, @PathVariable Long boardId, Model model) {
        GroupBoardDto groupBoardDto = groupService.findBoardById(boardId); // boardId를 사용하여 게시글 조회
        model.addAttribute("groupBoard", groupBoardDto); // 기존 게시글 정보 추가
        return "group/updateWrite"; // updateWrite 템플릿 반환
    }

    //모임 게시글 수정저장
    @PostMapping("/group/{groupId}/updateWrite/{boardId}")
    public String updateSave(
            @ModelAttribute GroupBoardDto groupBoardDto,
            @PathVariable Long groupId,
            @PathVariable Long boardId,
            RedirectAttributes redirectAttributes) {

        // postId를 사용하여 해당 게시글을 업데이트
        groupBoardDto.setGroupId(groupId);
        groupBoardDto.setBoardId(boardId);

        // 게시글 업데이트 수행
        GroupBoardDto updatedGroupBoard = groupService.updateBoard(boardId, groupBoardDto);

        // 메시지를 Flash Attribute로 추가하여 리다이렉트 시 전달
        redirectAttributes.addFlashAttribute("message", "게시글이 성공적으로 수정되었습니다.");

        // 수정 완료 후 게시글 목록 페이지로 리다이렉트
        return "redirect:/group/" + groupId + "/groupBoard";
    }
    //모임 관리 페이지
    @GetMapping("/group/{groupId}/groupAdmin")
    public String groupAdmin(@PathVariable Long groupId, Model model) {
        GroupDto groupDto = groupService.findGroupById(groupId); // 그룹 정보를 가져옵니다.
        model.addAttribute("groupDetail", groupDto); // 그룹 정보를 모델에 추가합니다.
        return "group/groupAdmin";
    }

    //회원관리페이지
    @GetMapping("/group/{groupId}/memberAdmin")
    public String memberAdmin(@PathVariable Long groupId, Model model,@AuthenticationPrincipal UserDetails userDetails) {

        GroupDto groupDto = groupService.findGroupById(groupId);
        model.addAttribute("groupOwner", groupDto.getCreatedBy());
        model.addAttribute("groupDetail", groupDto);
        return "group/memberAdmin";
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
        model.addAttribute("groupDetail", groupDto);
        return "group/applicationAdmin";
    }








}
