package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/start/join")
    public String signUp(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("categories",userService.getGroupCategories());
        return "start/join";
    }
    @PostMapping("/join")
    public String join(@ModelAttribute UserDto userDto) {
        userService.singUp(userDto, passwordEncoder);
        return "home";
    }

    @GetMapping("/start/login")
    public String login(Model model) {
        return "start/login";
    }
}
