package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final UserService userService;
    private final GroupService groupService;

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails){
        //신규모임 리스트
        List<GroupDto> groupDtoList=groupService.getAllGroups();
        model.addAttribute("groups",groupDtoList);

//        //맞춤모임 리스트
//        String username = userDetails.getUsername();
//        userDetails.
//        List<GroupDto> groupDto = groupService.getCustomGroupsForUser(username);
//        model.addAttribute("groups", groupDto);

        return "home";
    }

    @GetMapping("/helpline")
    public String helpline(){
        return "helpline";
    }

    @GetMapping("/introduce")
    public String introduce(){
        return "introduce";
    }

}
