package com.MeetingWeb.Service;

import com.MeetingWeb.Constant.TournamentStatus;
import com.MeetingWeb.Dto.*;
import com.MeetingWeb.Entity.*;
import com.MeetingWeb.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {
    //대회 관련 조회용 레포지토리
    private final TournamentSearchRepository tournamentSearchRepository;
    //대회 검색용 레포지토리
    private final TournamentRepository torunamentRepository;
    //대회 카테고리 조회용 레포지토리
    private final TournamentCategoryRepository tournamentCategoryRepository;
    private final TournamentRepository tournamentRepository;
    private final ProfileUploadService profileUploadService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;

    //대회 카테고리 가져오기
    public List<TournamentCategoryDto> getTournamentCategories() {
        List<TournamentCategory> categories = tournamentCategoryRepository.findAllByOrderByTournamentCategoryIdAsc();
        return categories.stream()
                .map(TournamentCategoryDto::of)  // of 메서드로 변환
                .collect(Collectors.toList());
    }

//    //카테고리 선택, 검색창에 입력한 값으로 대회 검색
//    public List<TrnDto> searchTournament(TournamentSearchDto tournamentSearchDto) {
//        return null;
//    }

//    private boolean hasLeaderRole() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null) {
//            return false;
//        }
//
//        // for 루프를 사용하여 권한 확인
//        for (GrantedAuthority authority : authentication.getAuthorities()) {
//            if (authority.getAuthority().equals("ROLE_LEADER")) {
//                return true;  // ROLE_LEADER 권한이 있는 경우 true 반환
//            }
//        }
//        return false;  // ROLE_LEADER 권한이 없는 경우 false 반환
//    }

    //대회 만들기
    @Transactional
    public void createTournament(TrnDto trnDto, User createdBy) throws Exception {
        TournamentCategory tournamentCategory = tournamentCategoryRepository.findById(trnDto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + trnDto.getCategory()));

        Groups group = groupRepository.findByCreatedById(createdBy.getId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자는 모임장이 아닙니다."));

        String tournamentImgUrl = profileUploadService.saveProfile(trnDto.getTournamentImg());

        Tournaments tournament = trnDto.toEntity(tournamentImgUrl, createdBy, tournamentCategory, group);

        LocalDateTime now = LocalDateTime.now();

        if (trnDto.getReceiptStart().isAfter(now)) {
            tournament.setStatus(TournamentStatus.UPCOMING);
        } else {
            tournament.setStatus(TournamentStatus.RECRUITING);
        }
        tournamentRepository.save(tournament);
        //참가팀 저장
        TournamentParticipant tournamentParticipant = new TournamentParticipant();
        tournamentParticipant.setTournament(tournament);
        tournamentParticipant.setGroup(group);
        tournamentParticipant.setMatchNumber(1);
        tournamentParticipantRepository.save(tournamentParticipant);
    }

    //대회 목록 조회(내림차순)
    public List<TrnDto> getTournamentList() {
        List<Tournaments> tournamentsList = tournamentRepository.findAllByOrderByCreatedAtDesc();
        return tournamentsList.stream()
                .map(TrnDto::of)  // of 메서드로 변환
                .collect(Collectors.toList());
    }

    //대회 상세 페이지
    public TrnDto getTournamentInfo(Long id) {
        Optional<Tournaments> tournament = tournamentRepository.findById(id);
        return tournament.map(TrnDto::of).orElse(null);
    }

    //신청 가능한 모임 목록
    public List<GroupDto> getMyGroupList(String userName) {
        User user = userRepository.findByUserName(userName);
        List<Groups> groups = groupRepository.findByCreatedBy(user);
        return groups.stream().map(GroupDto::of).collect(Collectors.toList());
    }

    //대회 신청
    public void applyTournament(Long tournamentId, Long groupId) {
        Tournaments tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("대회 조회실패"));
        Groups group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("모임 조회실패"));
        if (tournamentParticipantRepository.findByGroup_GroupId(groupId) == null) {
            TournamentParticipant participant = new TournamentParticipant();
            participant.setTournament(tournament);
            participant.setGroup(group);
            int count = tournamentParticipantRepository.getCount(tournament);
            System.out.println(count);
            participant.setMatchNumber(count+1);
            tournamentParticipantRepository.save(participant);
        }
    }

    //참가 모임
    public List<TournamentParticipantDto> getParticipantList(Long tournamentId) {
        List<TournamentParticipant> participants = tournamentParticipantRepository.findByTournamentIdOrderByMatchNumberAsc(tournamentId);

        // TournamentParticipant 객체에서 TournamentParticipantDto로 변환
        return participants.stream()
                .map(participant -> TournamentParticipantDto.of(participant.getGroup()))
                .collect(Collectors.toList());
    }

    //내가 만든 대회
    public List<TrnDto> getMyTournament(String userName) {
        User user = userRepository.findByUserName(userName);
        List<Tournaments> tournaments = tournamentRepository.findByCreatedBy(user);

        if (tournaments.isEmpty()) {
            return Collections.emptyList(); // 대회가 없는 경우 빈 리스트 반환
        }

        return tournaments.stream()
                .map(TrnDto::of)
                .collect(Collectors.toList());
    }

    //내가 가입한 모임이 참가하는 대회
    public List<TrnDto> getMyGroupTournament(String userName) {
        User user = userRepository.findByUserName(userName);
        List<Tournaments> tournaments = tournamentRepository.findByCreatedBy(user);

        if (tournaments.isEmpty()) {
            return Collections.emptyList(); // 대회가 없는 경우 빈 리스트 반환
        }

        List<Long> myCreateTournaments = tournaments.stream()
                .map(Tournaments::getId)
                .collect(Collectors.toList());

        List<Tournaments> myGroupTournament = tournamentRepository.findAllExcludingIds(myCreateTournaments);

        if (myGroupTournament.isEmpty()) {
            return Collections.emptyList(); // 내가 가입한 모임의 대회가 없는 경우 빈 리스트 반환
        }

        return myGroupTournament.stream()
                .map(TrnDto::of)
                .collect(Collectors.toList());
    }

    public void random(Long tournamentId) {
        Tournaments tournaments = tournamentRepository.findById(tournamentId).get();
        final int format = tournaments.getFormat();
        int[] a= new int[format];

        for (int i = 0; i < format; i++) {
            a[i] = i+1;
        }
        for(int i=0;i<a.length;i++){
            int j = (int)(Math.random()*a.length);
            int k = (int)(Math.random()*a.length);

            int tmp = a[j];
            a[j] = a[k];
            a[k] = tmp;
        }

        List<TournamentParticipant> participants = tournamentParticipantRepository.findByTournamentId(tournamentId);
        int i=0;
        for(TournamentParticipant tournamentParticipant : participants){
            tournamentParticipant.setMatchNumber(a[i++]);
            System.out.println(tournamentParticipant.getMatchNumber());
        }
        tournamentParticipantRepository.saveAll(participants);
    }
}
