package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.GroupCategoryDto;
import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.TournamentService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.swing.*;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class MainController {
    private final UserService userService;
    private final GroupService groupService;
    private final TournamentService tournamentService;

    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal UserDetails userDetails){
        //신규모임 리스트
        List<GroupDto> groupDtoList=groupService.getAllGroups();
        model.addAttribute("newGroups",groupDtoList);

        //맞춤모임 리스트
        if (userDetails != null) {
            String username = userDetails.getUsername();
            List<GroupDto> groupDto = groupService.getCustomGroupsForUser(username);
            model.addAttribute("groups", groupDto);
        }

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

    // 랭킹
    @GetMapping("/ranking")
    public String ranking(Model model) {
        List<GroupDto> rankingList = tournamentService.getRanking(1L);
        List<GroupCategoryDto> categories = userService.getGroupCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("rankingList", rankingList);
        return "ranking";
    }

    @GetMapping("/ranking/search")
    public String searchRanking(@RequestParam("categoryId") Long categoryId, Model model){
        List<GroupDto> rankingList = tournamentService.getRanking(categoryId);
        List<GroupCategoryDto> categories = userService.getGroupCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("rankingList", rankingList);
        return "ranking";
    }

}
