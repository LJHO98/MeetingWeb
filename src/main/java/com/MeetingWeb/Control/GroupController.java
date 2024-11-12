package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.GroupApplicationDto;
import com.MeetingWeb.Dto.GroupCategoryDto;
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
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/content")
    public String getGroup(Model model) {
        GroupDto groupDto = groupService.getGroupById(2L);
        model.addAttribute("groupDto", groupDto);
        return "group/viewContent";
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
    public String groupDetail(@PathVariable Long id, Model model) {
        GroupDto groupDto = groupService.findGroupById(id);
        model.addAttribute("groupDetail", groupDto);
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

        String message = isJoined ? "신청이 완료되었습니다." : "이미 가입한 모임입니다.";
        try {
            message = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "redirect:/group/" + groupId + "?message=" + message;
    }
}
