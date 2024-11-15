package com.MeetingWeb.Control;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Dto.GroupManageDto;
import com.MeetingWeb.Dto.TournamentManageDto;
import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Service.AdminService;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.TournamentService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.engine.groups.Group;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;
    private final GroupService groupService;
    private final TournamentService tournamentService;


    @GetMapping("/admin/main")
    public String showUserChart(Model model) {
        List<Integer> userCount = adminService.userCount();
        List<String> labels = adminService.monthLabels();


        // 특정 성별의 사용자 수 가져오기
        int maleCount = adminService.getUserCountByGender(Gender.MALE);
        int femaleCount = adminService.getUserCountByGender(Gender.FEMALE);
        int noneCount = adminService.getUserCountByGender(Gender.OTHER);


        long totalGroupsCount = adminService.getTotalGroupsCount();
        long totalTournaments = adminService.getTotalTournaments();



        // Thymeleaf로 전달
        model.addAttribute("totalTournaments",totalTournaments);
        model.addAttribute("totalGroupsCount", totalGroupsCount);
        model.addAttribute("maleCount", maleCount);
        model.addAttribute("femaleCount", femaleCount);
        model.addAttribute("noneCount", noneCount);
        model.addAttribute("monthLabels", labels);
        model.addAttribute("month",userCount);



        return "admin/main"; // 차트를 표시할 HTML 템플릿 반환
    }
    @GetMapping("/admin/userControl")
    public String userControl(Model model) {
        // 모든 사용자 정보 가져오기
        List<User> users = adminService.getAllUsers();

        // 모델에 데이터 담아서 템플릿으로 전달
        model.addAttribute("users", users);

        // users.html 템플릿 반환
        return "/admin/userControl";
    }


    @GetMapping("/admin/groupControl")
    public String groupControl(Model model) {
        List<GroupManageDto> groups = adminService.getAllGroups();
        model.addAttribute("groups", groups);
        return "/admin/groupControl";
    }


    @GetMapping("/admin/tournamentControl")
    public String tournamentControl(Model model) {
        List<TournamentManageDto> tournaments = adminService.getAllTournaments();

        model.addAttribute("tournaments", tournaments);
        return "/admin/tournamentControl";
    }




    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);  // 서비스 메서드를 호출하여 유저 삭제
        return "redirect:/admin/userControl";  // 삭제 후 유저 목록 페이지로 리다이렉트
    }
    @PostMapping("/groups/delete")
    public String deleteGourp(@RequestParam Long groupId) {
        groupService.deleteGroup(groupId);  // 서비스 메서드를 호출하여 유저 삭제
        return "redirect:/admin/groupControl";  // 삭제 후 유저 목록 페이지로 리다이렉트
    }
    @PostMapping("/tournaments/delete")
    public String deleteTournament(@RequestParam Long tournamentsId) {
        tournamentService.deleteTournament(tournamentsId);  // 서비스 메서드를 호출하여 유저 삭제
        return "redirect:/admin/tournamentControl";  // 삭제 후 유저 목록 페이지로 리다이렉트
    }
}
