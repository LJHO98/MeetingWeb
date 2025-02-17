package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.PasswordChangeRequest;
import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Dto.UserProfileDto;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.UserRepository;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

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
    @PostMapping("/login/searchId")
    public @ResponseBody ResponseEntity<String> searchLoginId(String email) {
        User user = userService.findByEmail(email);
        if (user != null) {
            String id = user.getUserName();
            return ResponseEntity.ok(id); // 성공 시 id 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found"); // 사용자 없을 경우
        }
    }

    @PostMapping("/login/searchPw")
    public @ResponseBody ResponseEntity<String> searchLoginPw(@RequestBody PasswordChangeRequest request) {
        String email = request.getPwEmail();
        String pw = request.getPw();
        String pwCheck = request.getPwCheck();

        User user = userService.findByEmail(email);

        System.out.println("dddddww"+email+pw+pwCheck);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        // 비밀번호와 비밀번호 확인이 일치하는지 검증
        if (!pw.equals(pwCheck)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }

        // 새 비밀번호를 암호화하여 저장
        user.setPassword(passwordEncoder.encode(pw));
        //userService.save(user); // 비밀번호 저장
        userService.changePassword(pw, email);
        return ResponseEntity.ok("Password changed successfully");
    }

    @PostMapping("/start/check-username")
    @ResponseBody public boolean checkUsername(@RequestParam String userName) {
        return !userService.isUserNameTaken(userName);
    }

    @GetMapping("/start/login")
    public String login() {
        return "start/login";
    }

    // 로그인 실패 - 아이디나 비밀번호 틀린경우
    @GetMapping("/start/login/error")
    public String loginFailHandler(Model model) {
        model.addAttribute("loginFailMsg", "아이디 또는 비밀번호가 올바르지 않습니다.");
        return "start/login";
    }

    @GetMapping("/start/myPage")
    public String viewMemberProfile(Principal principal , Model model) {
        // principal이 null인 경우 (로그인되지 않은 경우)_회원탈퇴
        if (principal == null) {
            return "redirect:/start/login"; // 로그인 페이지로 리다이렉트
        }
        //로그인할때 사용한 Id
        String userName = principal.getName();
        // 사용자 정보를 가져와서 모델에 추가
        model.addAttribute("userProfileDto", userService.getUserProfile(userName));
        model.addAttribute("categories",userService.getGroupCategories());

        // start/mypage.html 페이지로 이동합니다.
        return "start/myPage";
    }

    @PostMapping("/start/updateProfile")
    public String updateMemberProfile(@ModelAttribute UserProfileDto userProfileDto, Model model) {
        try {
            // 사용자 프로필 업데이트
            userService.updateUserProfile(userProfileDto);
            // 업데이트 후 마이페이지로 리다이렉트
            return "redirect:/start/myPage";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "사용자를 찾을 수 없습니다. 다시 시도해 주세요.");
            return "start/myPage";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/start/deleteAccount")
    @ResponseBody
    public ResponseEntity<String> deleteAccount(Principal principal) {
        // 현재 로그인한 사용자 이름을 가져옴
        String userName = principal.getName();

        try {
            // 사용자 삭제 서비스 호출
            userService.deleteAccountByUserName(userName);
            return ResponseEntity.ok("회원 탈퇴가 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원 탈퇴에 실패했습니다. 다시 시도해주세요.");
        }
    }
}
