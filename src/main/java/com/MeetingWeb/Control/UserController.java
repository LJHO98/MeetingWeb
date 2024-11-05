package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/start/agreement")
    public String agreement(){
        return "start/agreement";
    }

    @GetMapping("/start/join")
    public String signUp(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("categories",userService.getGroupCategories());
        return "start/join";
    }

    @PostMapping("/start/join")
    public String join(@Valid @ModelAttribute UserDto userDto, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("categories",userService.getGroupCategories());
            return "start/join";  // 유효성 검사에 실패한 경우, 가입 페이지로 다시 이동
        }

        // 아이디 중복 검사
        if (userService.isUserNameTaken(userDto.getUserName())) {
            bindingResult.rejectValue("userName", "duplicate", "중복된 아이디입니다.");
            model.addAttribute("categories", userService.getGroupCategories());
            return "start/join";
        } else {
            model.addAttribute("message", "사용 가능한 아이디입니다.");
        }

        userService.singUp(userDto, passwordEncoder);
        return "redirect:/home";
    }

    @PostMapping("/start/check-username")
    @ResponseBody
    public boolean checkUsername(@RequestParam String userName) {
        return !userService.isUserNameTaken(userName);
    }

    @GetMapping("/start/login")
    public String login(Model model) {
        return "start/login";
    }
}
