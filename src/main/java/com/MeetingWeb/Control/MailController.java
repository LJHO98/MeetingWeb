package com.MeetingWeb.Control;

import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Service.EmailService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;

@Controller
@RequiredArgsConstructor
public class MailController {
    private final UserService userService;
    private final EmailService emailService;

    // 인증코드 전송(회원가입 이메일)
    @PostMapping("/mail")
    public @ResponseBody ResponseEntity<String> sendEmailPath(String email) throws MessagingException {
        try{
           userService.isExistEmail(email);
        }catch(IllegalStateException e1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("존재하지 않는 이메일입니다. 다시 입력하세요.");
        }
        emailService.sendEmail(email);
        return new ResponseEntity<String>("인증코드 전송, 이메일을 확인하세요", HttpStatus.OK);
    }
    @PostMapping("/join-Mail")
    public @ResponseBody ResponseEntity<String> sendJoinEmailPath(String email) throws MessagingException {
        try{
            userService.isExistEmail(email);
            userService.findByEmail(email);
        }catch(IllegalStateException e1){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 존재하는 이메일입니다. 다른 이메일을 입력해주세요.");
        }
        emailService.sendEmail(email);
        return new ResponseEntity<String>("인증코드 전송, 이메일을 확인하세요", HttpStatus.OK);
    }
    // 인증코드 인증
    @PostMapping("/verifyCode")
    public @ResponseBody ResponseEntity<String> verifyCode(@RequestParam("number") String number,
                                                           @RequestParam("email") String email) {
        if (emailService.verifyEmailCode(email, number)) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패: 잘못된 인증 코드입니다.");
    }
    // 인증코드 인증

    @PostMapping("/join-VerifyCode")
    @CacheEvict(value = "event" , allEntries = false)
    public @ResponseBody ResponseEntity<String> joinVerifyCode(@RequestParam("number") String number,
                                                           @RequestParam("email") String email) {
        if (emailService.verifyEmailCode(email, number)) {
            return ResponseEntity.ok("인증 성공");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 실패: 잘못된 인증 코드입니다.");
    }

}
