package com.MeetingWeb.Service;


import com.MeetingWeb.Util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;
    @Value("${spring.mail.username}")
    private String configEmail;

    private String createdCode() {
        int leftLimit = 48; // number '0'
        int rightLimit = 122; // alphabet 'z'
        int targetStringLength = 6;
        Random random = new Random();

        //Stream
        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <=57 || i >=65) && (i <= 90 || i>= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    //이메일 내용 작성
    private MimeMessage createEmailForm(String email) throws MessagingException {

        String authCode = createdCode();
        MimeMessage message = javaMailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, email);
        message.setSubject("안녕하세요 잇다방입니다.");
        message.setFrom(configEmail);

        String body="";
        body += "<h3>" + "이메일 인증코드는 " + "</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>" + "입니다." + "</h3>";
        body += "<h3>" + "해당 인증코드는 3분 후 만료됩니다." +"</h3>";

        message.setText(body , "utf-8", "html");

        redisUtil.setDataExpire(email, authCode, 60*3L);

        return message;
    }
    @Async
    public void sendEmail(String email) throws MessagingException {
        if (redisUtil.existData(email)) {
            redisUtil.deleteData(email);
        }

        MimeMessage emailForm = createEmailForm(email);

        javaMailSender.send(emailForm);
    }
    //redis의 저장되어있는 인증코드와 사용자가 입력한 인증코드 비교
    public Boolean verifyEmailCode(String email, String code) {
        String codeFoundByEmail = redisUtil.getData(email);
        if (codeFoundByEmail == null) {
            return false;
        }
        return codeFoundByEmail.equals(code);
    }

}


