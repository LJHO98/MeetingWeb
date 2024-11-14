package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.NoticeDto;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.NoticeService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final GroupService groupService;
    private final UserService userService;

    // 공지사항 저장
    @PostMapping("/save")
    public ResponseEntity<String> saveNotice(@RequestParam String content, @RequestParam Long groupId, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        if(groupService.isGroupOwner(userId, groupId)){
            try {
                noticeService.saveNotice(content, groupId);
                return ResponseEntity.ok("공지사항이 저장되었습니다.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장에 실패했습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("작성 권한이 없습니다.");
    }

    // 공지사항 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNotice(@PathVariable Long id) {
        try {
            noticeService.deleteNotice(id);
            return ResponseEntity.ok("공지사항이 삭제되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("삭제에 실패했습니다.");
        }
    }
    @GetMapping("/test")
    public String test(Model model){
        model.addAttribute("notice", new NoticeDto());
        return "group/noticeTest";
    }

    // 모든 공지사항 반환
//    @GetMapping("/all")
//    public ResponseEntity<List<NoticeDto>> getAllNotices(@RequestParam("groupId") Long groupId) {
//        List<NoticeDto> notices = noticeService.getGroupNotices(groupId);
//
//        return ResponseEntity.ok(notices);
//    }

    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getAllNotices(
            @RequestParam("groupId") Long groupId,
            @AuthenticationPrincipal UserDetails userDetails) {

        List<NoticeDto> notices = noticeService.getGroupNotices(groupId);
        Long userId = userService.findByUserName(userDetails.getUsername()).getId();
        boolean isGroupOwner = groupService.isGroupOwner(userId, groupId);

        // 응답 데이터를 Map으로 구성
        Map<String, Object> response = new HashMap<>();
        response.put("notices", notices);
        response.put("isGroupOwner", isGroupOwner);

        return ResponseEntity.ok(response);
    }
}

