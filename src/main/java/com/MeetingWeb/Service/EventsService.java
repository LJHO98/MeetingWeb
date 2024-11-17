package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.EventsDto;
import com.MeetingWeb.Entity.Events;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Repository.EventsRepository;
import com.MeetingWeb.Repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventsService {
    private final EventsRepository eventsRepository;

    @Value("${eventImgPath}")
    private String eventImgPath;
    @Value("${eventUploadPath}")
    private String eventUploadPath;

    //이미지경로 설정
    public String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        // 파일 저장 디렉토리

        String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(eventImgPath , fileName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, imageFile.getBytes());

        // 반환할 URL 경로
        return eventUploadPath +"/"+ fileName;
    }
    //일정저장
    public Events save(Events events) {
        return eventsRepository.save(events);
    }
    //일정 리스트
    public List<EventsDto> getEventsByGroupId(Long groupId) {
        List<Events> events = eventsRepository.findByGroup_GroupId(groupId); // 그룹 ID로 조회
        List<EventsDto>eventsDtoList=new ArrayList<>();
        for (Events event : events) {
           eventsDtoList.add(EventsDto.of(event));
        }
        return eventsDtoList;
    }
    //일정삭제
    public void deleteEvent(Long eventId) {
        eventsRepository.deleteById(eventId); // 이벤트 삭제
    }


}
