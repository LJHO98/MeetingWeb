package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.GroupRepository;
import com.MeetingWeb.Repository.TournamentRepository;
import com.MeetingWeb.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final TournamentRepository tournamentRepository;

    // 모든 사용자 정보를 반환 (DTO 없이 엔티티 직접 반환)
    public List<User> getAllUsers() {
        // DB에서 모든 사용자 정보 조회
        return userRepository.findAll();
    }
}
