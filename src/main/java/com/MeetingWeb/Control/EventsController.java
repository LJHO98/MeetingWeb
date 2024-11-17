package com.MeetingWeb.Control;


import com.MeetingWeb.Dto.EventsDto;
import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Entity.Events;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Service.EventsService;
import com.MeetingWeb.Service.FileStorageService;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EventsController {
    private final GroupService groupService;
    private final UserService userService;
    private final EventsService eventsService;
    private final FileStorageService fileStorageService;

    //일정저장
    @PostMapping("/events/{groupId}/save")
    public String saveEvent(
            @ModelAttribute EventsDto eventsDto,
            @PathVariable Long groupId,
            @AuthenticationPrincipal UserDetails userDetails,Model model) {
        try {
            // 그룹 및 유저 조회
            Groups group = groupService.findGroupId(groupId);
            User user = userService.findByUserName(userDetails.getUsername());

            // 이미지 파일 저장 및 URL 반환
            String imageUrl = eventsService.saveImage(eventsDto.getImage());

            // 이벤트 저장
            Events event = eventsDto.toEntity(group, user, imageUrl);
            eventsService.save(event);


            return "redirect:/groupAdmin/" + groupId + "/eventAdmin";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }
    //일정삭제
    @PostMapping("/events/{eventId}/delete/{groupId}")
    public String deleteEvent(@PathVariable Long eventId, @PathVariable Long groupId) {
        eventsService.deleteEvent(eventId);
        return "redirect:/groupAdmin/" + groupId + "/eventAdmin"; // 목록으로 리다이렉트
    }








}
