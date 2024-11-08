package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.GroupApplicationDto;
import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;

    @PostMapping("/group/createGroup")
    public String createGroup(@Valid GroupDto groupDto,BindingResult bindingResult, Model model, @AuthenticationPrincipal UserDetails userDetails) throws IOException {
       if(bindingResult.hasErrors()) {
           model.addAttribute("categories", userService.getGroupCategories());
           return "group/createGroup";
       }
        // 필요한 경우 User 객체로 캐스팅
        String username = userDetails.getUsername();
        User createdBy = userService.findByUserName(username); // User 서비스에서 User 객체 조회
        groupService.createGroup(groupDto, createdBy);
        return "home";
    }

    @GetMapping("/group/createGroup")
    public String showCreateForm(Model model) {
        model.addAttribute("categories",userService.getGroupCategories());
        model.addAttribute("groupDto", new GroupDto());
        return "group/createGroup";
    }

    @GetMapping("/content")
    public String getGroup(Model model) {
        GroupDto groupDto = groupService.getGroupById(2L); // 그룹 조회
        model.addAttribute("groupDto", groupDto);
        return "group/viewContent"; // viewContent.html 페이지로 이동
    }
    @GetMapping("/group/list") // 모임 목록
    public String groupList(Model model) {
        List<GroupDto> groupDtoList = groupService.getAllGroups();
        model.addAttribute("groupList", groupDtoList);
        return "group/groupList";
    }
    @GetMapping("/group/{id}")
    public String groupDetail(@PathVariable Long id, Model model) {
        GroupDto groupDto = groupService.findGroupById(id);
        model.addAttribute("groupDetail", groupDto);
        return "group/groupDetail";
    }
    @PostMapping("/group/join/{groupId}")//자유가입
    public String joinGroup(@PathVariable Long groupId, @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        boolean isJoined = groupService.joinGroup(groupId, username);

        String message = isJoined ? "모임에 성공적으로 가입되었습니다." : "이미 가입한 모임입니다.";
        // 쿼리 파라미터로 메시지를 전달
        // URL 인코딩을 사용하여 메시지를 쿼리 파라미터로 전달
        try {
            message = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:/group/" + groupId + "?message=" + message;
    }

    @PostMapping("/group/approval/{groupId}")//승인가입
    public String approveGroup(@PathVariable Long groupId, @AuthenticationPrincipal UserDetails userDetails, @ModelAttribute GroupApplicationDto groupApplicationDto) {


        String username = userDetails.getUsername();
        boolean isJoined = groupService.approve(groupId, username, groupApplicationDto);

        String message = isJoined ? "신청이 완료되었습니다." : "이미 가입한 모입입니다.";

        try {
            message = URLEncoder.encode(message,"UTF-8");
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "redirect:/group/" + groupId + "?message=" + message;

    }
}
