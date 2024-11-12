package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Service.AdminService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/admin/main")
    public String main() {
        return "admin/main";
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
    public String groupControl() {
        return "/admin/groupControl";
    }
    @GetMapping("/admin/tournamentControl")
    public String tournamentControl() {
        return "/admin/tournamentControl";
    }




    @PostMapping("/users/delete")
    public String deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);  // 서비스 메서드를 호출하여 유저 삭제
        return "/admin/userControl";  // 삭제 후 유저 목록 페이지로 리다이렉트
    }
}
