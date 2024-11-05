package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
