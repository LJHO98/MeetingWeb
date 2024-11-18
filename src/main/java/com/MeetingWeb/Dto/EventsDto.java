package com.MeetingWeb.Dto;

import com.MeetingWeb.Entity.Events;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class EventsDto {

    private Long id;
    private Long groupId; // 그룹 ID
    private Long userId; // 유저 ID
    private MultipartFile image; // 이미지 파일
    private String imageUrl;
    @NotNull(message = "필수 입력입니다.")
    @Size(min = 5, max=15, message = "5자에서 15자 이내로 입력해주세요.")
    private String title;
    @Size(min=10,max=50, message = "10자에서 50자 이내로 입력해주세요.")
    private String description;//내용

    @NotNull(message = "필수 입력입니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime date;//일정

    @NotNull(message = "필수 입력입니다.")
    @Size(min=5, max=25, message = "5자에서 25자 이내로 입력해주세요")
    private String location;//위치

    @Min(value = 2, message = "2명 이상만 가능합니다.")
    @Max(value = 30, message = "30명 이하만 가능합니다.")
    private Integer capacity;//정원

    // DTO -> Entity 변환
    public Events toEntity(Groups group, User user, String imageUrl) {
        Events events = new Events();
        events.setId(this.id);
        events.setGroup(group); // Group 엔티티 설정
        events.setUser(user); // User 엔티티 설정
        events.setImageUrl(imageUrl); // 이미지 URL 설정
        events.setTitle(this.title);
        events.setDescription(this.description);
        events.setDate(this.date);
        events.setLocation(this.location);
        events.setCapacity(this.capacity);
        return events;
    }

    // Entity -> DTO 변환
    public static EventsDto of(Events events) {
        EventsDto eventsDto = new EventsDto();
        eventsDto.setId(events.getId());
        eventsDto.setGroupId(events.getGroup().getGroupId());
        eventsDto.setUserId(events.getUser().getId());
        eventsDto.setTitle(events.getTitle());
        eventsDto.setDescription(events.getDescription());
        eventsDto.setDate(events.getDate());
        eventsDto.setLocation(events.getLocation());
        eventsDto.setCapacity(events.getCapacity());
        eventsDto.setImageUrl(events.getImageUrl());
        return eventsDto;
    }
}
