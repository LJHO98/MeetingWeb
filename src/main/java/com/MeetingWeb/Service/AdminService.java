package com.MeetingWeb.Service;

import com.MeetingWeb.Constant.Gender;
import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Dto.GroupManageDto;
import com.MeetingWeb.Dto.TournamentManageDto;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.GroupRepository;
import com.MeetingWeb.Repository.TournamentRepository;
import com.MeetingWeb.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    public List<GroupManageDto> getAllGroups() {
        List<GroupManageDto> groups = groupRepository.findAll().stream()
                .map(GroupManageDto::of  ).collect(Collectors.toList());

        return groups;
    }

    public List<TournamentManageDto> getAllTournaments() {
        List<TournamentManageDto> tournaments = tournamentRepository.findAll().stream()
                .map(TournamentManageDto::of  ).collect(Collectors.toList());


        return tournaments;
    }


    //대쉬보드에 띄어줄 달 별 가입 회원수 조회 메서드
    public List<Integer> userCount() {
        List<Integer> userCount = new ArrayList<>();
        List<Integer> months = new ArrayList<>();

        // 현재 날짜의 월 구하기
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();


        // 현재 월 포함 이전 4개월의 월 구하기
        for (int i = 0; i < 4; i++) {
            int m = currentMonth - i;
            if (m <= 0) {
                m += 12; // 이전 년도의 월로 이동
            }
            months.add(m);
        }

        // 각 월별 사용자 수 구하기
        for (int m : months) {
            userCount.add(userRepository.countUserByCreatedAt(m));
        }
        Collections.reverse(userCount);
        return userCount;
    }
    public List<String> monthLabels(){
        List<String> labels = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 4; i++) {
            int month = currentDate.minusMonths(i).getMonthValue();
            labels.add(month + "월");
        }
        Collections.reverse(labels); // 순서 뒤집기
        return labels;
    }


    public int getUserCountByGender(Gender gender) {

        return userRepository.countUserByGender(gender);
    }


    // 전체 회원 수 가져오기
    public long getTotalUserCount() {
        return userRepository.countTotalUsers();
    }


    public long getTotalTournaments(){
        return tournamentRepository.countTotalTournaments();
    }




}
