package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final UserService userService;
    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/helpline")
    public String helpline(){
        return "helpline";
    }


}
