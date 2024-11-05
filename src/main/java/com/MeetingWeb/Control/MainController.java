package com.MeetingWeb.Control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/helpline")
    public String helpline(){
        return "helpline";
    }
}
