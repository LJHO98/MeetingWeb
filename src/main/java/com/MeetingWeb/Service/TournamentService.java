package com.MeetingWeb.Service;

import com.MeetingWeb.Constant.Role;
import com.MeetingWeb.Constant.TournamentStatus;
import com.MeetingWeb.Dto.*;
import com.MeetingWeb.Entity.*;
import com.MeetingWeb.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {
    //대회 카테고리 조회용 레포지토리
    private final TournamentCategoryRepository tournamentCategoryRepository;
    private final TournamentRepository tournamentRepository;
    private final ProfileUploadService profileUploadService;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;
    private final TournamentParticipantRepository tournamentParticipantRepository;
    private final UserService userService;

    //대회 카테고리 가져오기
    public List<TournamentCategoryDto> getTournamentCategories() {
        List<TournamentCategory> categories = tournamentCategoryRepository.findAllByOrderByTournamentCategoryIdAsc();
        return categories.stream()
                .map(TournamentCategoryDto::of)  // of 메서드로 변환
                .collect(Collectors.toList());
    }

    //대회 만들기
    @Transactional
    public void createTournament(TrnDto trnDto, String userName) throws IOException {
        TournamentCategory tournamentCategory = tournamentCategoryRepository.findById(trnDto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + trnDto.getCategory()));

        User createdBy = userRepository.findByUserName(userName);
        if(trnDto.getGroupId()==null){
            throw new EntityNotFoundException("모임을 만들지 않으면 대회 생성이 불가능합니다.");
        }
            Groups group = groupRepository.findById(trnDto.getGroupId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 사용자는 모임장이 아닙니다."));


        String tournamentImgUrl = profileUploadService.saveProfile(trnDto.getTournamentImg());

        Tournaments tournament = trnDto.toEntity(tournamentImgUrl, createdBy, tournamentCategory, group);

        LocalDateTime now = LocalDateTime.now();

        if (trnDto.getReceiptStart().isAfter(now)) {
            tournament.setStatus(TournamentStatus.UPCOMING);
        } else {
            tournament.setStatus(TournamentStatus.RECRUITING);
        }

        //생성시 만든 모임도 참가팀 저장
        TournamentParticipant tournamentParticipant = new TournamentParticipant();
        tournamentParticipant.setTournament(tournament);
        tournamentParticipant.setGroup(group);
        tournamentParticipant.setMatchNumber(1);
        tournamentParticipant.setBracketNumber(1); // 새로운 브라켓 번호
        tournamentParticipantRepository.save(tournamentParticipant);

        //대회 만든팀도 카운트, 대회 저장
        tournament.setCurrentTeamCount(1);
        tournamentRepository.save(tournament);

    }

    //대회 수정
    public void updateTournament(TrnDto trnDto, Long createdBy) {
        User user = userService.findByUserId(createdBy);
        TournamentCategory tournamentCategory = tournamentCategoryRepository.findById(trnDto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + trnDto.getCategory()));
        Groups group = groupRepository.findById(trnDto.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자는 모임장이 아닙니다."));
        Tournaments tournament = trnDto.toEntity(trnDto.getTournamentImgUrl(),user, tournamentCategory, group);
        tournamentRepository.save(tournament);
    }


    //대회 목록 조회(내림차순)
    public List<TrnDto> getTournamentList() {
        List<Tournaments> tournamentsList = tournamentRepository.findAllByOrderByCreatedAtDesc();
        return tournamentsList.stream()
                .map(TrnDto::of)  // of 메서드로 변환
                .collect(Collectors.toList());
    }
    //대회 검색
    public List<TrnDto> searchTournament(Long categoryId, String inputText) {
        List<Tournaments> tournaments = tournamentRepository.searchByCategoryAndText(categoryId, inputText);

        // Tournaments 엔티티를 TrnDto로 변환
        return tournaments.stream().map(TrnDto::of).collect(Collectors.toList());
    }

    //대회 상세 페이지
    public TrnDto getTournamentInfo(Long id) {
        Optional<Tournaments> tournament = tournamentRepository.findById(id);
        return tournament.map(TrnDto::of).orElse(null);
    }


    //대회 신청 가능한 모임 목록
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
        if (tournament.getStatus()==TournamentStatus.COMPLETED || tournament.getStatus()==TournamentStatus.IN_PROGRESS) {
            throw new NonUniqueResultException("이미 해당 대회에 신청했거나 대회가 신청이 가능한 상태가 아닙니다.");
        } else if (tournamentParticipantRepository.findByGroupAndTournament(group, tournament) == null && tournament.getStatus() == TournamentStatus.RECRUITING) {
            if (tournament.getCurrentTeamCount() < tournament.getFormat()) {
                TournamentParticipant participant = new TournamentParticipant();
                participant.setTournament(tournament);
                participant.setGroup(group);
                int count = tournamentParticipantRepository.getCount(tournament);
                System.out.println("count = " + count);
                participant.setMatchNumber(count + 1);
                int matchNumber = participant.getMatchNumber();
                participant.setBracketNumber((matchNumber + 1) / 2);

                //처음에 등록할때 팀 카운트
                if (tournamentParticipantRepository.findByGroupAndTournament(group, tournament) == null) {
                    int teamCount = tournament.getCurrentTeamCount();
                    tournament.setCurrentTeamCount(teamCount + 1);
                }
                tournamentParticipantRepository.save(participant);
                tournamentRepository.save(tournament);
            }
        }else {
            throw new NonUniqueResultException("이미 해당 대회에 신청했거나 대회가 신청이 가능한 상태가 아닙니다.");
        }

    }


    //참가 모임(대회진행을 위한 리스트)
    public List<TournamentParticipantDto> getParticipantList(Long tournamentId) {
        List<TournamentParticipant> participants = tournamentParticipantRepository.findByTournamentIdOrderByMatchNumberAsc(tournamentId);

        // TournamentParticipant 객체에서 TournamentParticipantDto로 변환
        return participants.stream()
                .map(participant -> TournamentParticipantDto.of(participant.getGroup(), participant))
                .collect(Collectors.toList());
    }

    //참가 모임
    public List<TournamentParticipantDto> getParticipants(Long tournamentId) {
        Tournaments tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("대회 조회실패"));
        List<TournamentParticipant> participants = tournamentParticipantRepository.findDistinctParticipantsByTournament(tournament);

        return participants.stream()
                .map(participant -> TournamentParticipantDto.of(participant.getGroup(), participant))
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
    public List<TrnDto> getMyGroupTournament(String userName, List<TrnDto> myTournament) {
        User user = userRepository.findByUserName(userName);

        List<Tournaments> myGroupTournament = tournamentRepository.findTournamentsByUser(user);
        Iterator<Tournaments> it = myGroupTournament.iterator();

        while(it.hasNext()){
            Tournaments tournaments1 = it.next();
            for(int k=0; k<myTournament.size(); k++){
                if(tournaments1.getId() == myTournament.get(k).getTournamentId()){
                        it.remove();
                }
            }
        }

        if (myGroupTournament.isEmpty()) {
            return Collections.emptyList(); // 내가 가입한 모임의 대회가 없는 경우 빈 리스트 반환
        }

        return myGroupTournament.stream()
                .map(TrnDto::of)
                .collect(Collectors.toList());
    }

    //모임이 참가하는 대회
    public List<TrnDto> getGroupTournament(Long groupId) {
        Groups group = groupRepository.findByGroupId(groupId);
        List<Tournaments> groupTournament = tournamentRepository.findDistinctTournamentsByGroup(group);
        if (groupTournament.isEmpty()) {
            return Collections.emptyList();
        }
        return groupTournament.stream()
                .map(TrnDto::of)
                .collect(Collectors.toList());
    }

    //대회 팀섞기
    public void shuffle(Long tournamentId) {

        Tournaments tournaments = tournamentRepository.findById(tournamentId).get();
        TournamentStatus status = tournaments.getStatus();

        //대회상태가 모집 마감상태일때만 작동(경기 시작 전)
        if(status.equals(TournamentStatus.CLOSED) || status.equals(TournamentStatus.UPCOMING)) {
            final int format = tournaments.getFormat();
            int[] a = new int[format];

            for (int i = 0; i < format; i++) {
                a[i] = i + 1;
            }
            for (int i = 0; i < a.length; i++) {
                int j = (int) (Math.random() * a.length);
                int k = (int) (Math.random() * a.length);

                int tmp = a[j];
                a[j] = a[k];
                a[k] = tmp;
            }

            List<TournamentParticipant> participants = tournamentParticipantRepository.findByTournamentId(tournamentId);
            int i = 0;
            for (TournamentParticipant tournamentParticipant : participants) {
                tournamentParticipant.setMatchNumber(a[i++]);
                System.out.println(tournamentParticipant.getMatchNumber());
            }
            tournamentParticipantRepository.saveAll(participants);
        }else {
            throw new RuntimeException("대회 진행중 혹은 종료된 대회입니다.");
        }
    }

    //대회 결과 선택
    public void selectResult(Long winId, Long tournamentId, int scoreA,int scoreB ,int matchNumber) {
        Tournaments tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("대회 조회실패"));
        Groups group = groupRepository.findById(winId)
                .orElseThrow(() -> new EntityNotFoundException("모임 조회실패"));
        int currentCount = tournamentParticipantRepository.getCount(tournament);

        if(tournament.getStatus()!=TournamentStatus.IN_PROGRESS){
            throw new IllegalArgumentException("대회가 아직 시작되지 않아 경기 결과 선택이 불가능합니다.");
        }

        if(currentCount == (tournament.getFormat()*2) -2) {
            //패자 매치넘버
            int loserNum = 0;
            if(matchNumber%2==0){
                loserNum = matchNumber-1;
            }else{
                loserNum = matchNumber+1;
            }

            //대회 상태 종료로 변경
            tournament.setStatus(TournamentStatus.COMPLETED);
            TournamentParticipant winner = tournamentParticipantRepository.findByGroupAndTournamentAndMatchNumber(group, tournament, matchNumber);
            TournamentParticipant loser = tournamentParticipantRepository.findByTournamentAndMatchNumber(tournament, loserNum);

            //우승자 기록
            if(winner.getMatchResult()==null) {
                //스코어 저장
                if(scoreA>scoreB){
                    winner.setScore(scoreA);
                    loser.setScore(scoreB);
                }else{
                    winner.setScore(scoreB);
                    winner.setScore(scoreA);
                }
                //결과 저장
                winner.setMatchResult("승");
                loser.setMatchResult("패");
                // 경기 완료 상태 업데이트
                winner.setCompleted(true);
                loser.setCompleted(true);

                TournamentParticipant finalWinner = new TournamentParticipant();
                finalWinner.setTournament(tournament);
                finalWinner.setGroup(group);
                finalWinner.setMatchNumber(currentCount + 1);
                finalWinner.setBracketNumber(currentCount+1); // 최종 브라켓 번호


                //승리한 모임의 승리 횟수 증가
                int winCount = group.getWin();
                group.setWin(winCount + 1);
                groupRepository.save(group);
                tournamentRepository.save(tournament);
                tournamentParticipantRepository.save(finalWinner);
            }

        }else if(currentCount < (tournament.getFormat()*2)-1){
            //결과 기록
            //패자 매치넘버
            int loserNum = 0;
            if(matchNumber%2==0){
                loserNum = matchNumber-1;
            }else{
                loserNum = matchNumber+1;
            }
            System.out.println("loserNum "+loserNum);
            TournamentParticipant winner = tournamentParticipantRepository.findByGroupAndTournamentAndMatchNumber(group, tournament, matchNumber);
            TournamentParticipant loser = tournamentParticipantRepository.findByTournamentAndMatchNumber(tournament, loserNum);
            if (winner == null) {
                throw new EntityNotFoundException("해당 참가자를 찾을 수 없습니다.");
            }

            System.out.println("패자 스코어" + loser.getScore());

            //스코어 저장
            if(scoreA>scoreB){
                winner.setScore(scoreA);
                loser.setScore(scoreB);
            }else{
                winner.setScore(scoreB);
                winner.setScore(scoreA);
            }

            //다음 매치넘버 부여
            if(winner.getMatchResult() == null && loser.getMatchResult() == null) {
                System.out.println("winner"+winner.getMatchNumber());
                System.out.println("loser"+loser.getMatchResult());
                //결과 저장
                winner.setMatchResult("승");
                loser.setMatchResult("패");
                // 경기 완료 상태 업데이트
                winner.setCompleted(true);
                loser.setCompleted(true);

                TournamentParticipant participant = new TournamentParticipant();
                participant.setTournament(tournament);
                participant.setGroup(group);
                participant.setMatchNumber(currentCount + 1);
                participant.setBracketNumber((participant.getMatchNumber() + 1) / 2); // 새로운 브라켓 번호
                tournamentParticipantRepository.save(participant);
            }else{
                //결과 저장
                winner.setMatchResult("승");
                loser.setMatchResult("패");
                winner.setCompleted(true);
                loser.setCompleted(true);
            }
        }else{
            throw new IllegalArgumentException("대회 종료되었습니다.");
        }

    }

    public List<GroupDto> getRanking(Long categoryId){
        if(categoryId==null){
            categoryId=1L;
        }
        List<Groups> rankingList = groupRepository.findByCategoryOrderByWinDesc(categoryId);
        return rankingList
                .stream()
                .map(GroupDto::of)
                .collect(Collectors.toList());
    }

    public boolean isOkCreateBracket(Long tournamentId) {
        Tournaments tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("대회 조회실패"));
        return tournament.getCurrentTeamCount() == tournament.getFormat();
    }

    public boolean isStartTournament(Long tournamentId) {
        Tournaments tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new EntityNotFoundException("대회 조회실패"));
        return tournament.getStatus().equals(TournamentStatus.IN_PROGRESS);
    }
    public void deleteTournament(Long id){
        tournamentRepository.deleteById(id);
    }

    public boolean isGroupOwner(String name) {
        User user = userService.findByUserName(name);
        Groups group = groupRepository.findByCreatedById(user.getId()).orElse(null);
        if(group==null){
            return false;
        }
        return user.getRole().equals(Role.LEADER);
    }

    public boolean isUserLeader(Long tournamentId, String username) {
        // 데이터베이스에서 대회 ID와 리더 정보 확인
        Tournaments tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대회가 없습니다."));
        User user = userService.findByUserName(username);
        return tournament.getCreatedBy().getId().equals(user.getId());
    }

    public boolean isExistTournament(Long tournamentId) {
        System.out.println("서비스 : "+tournamentId);
        Tournaments tournaments = tournamentRepository.findById(tournamentId).orElse(null);
        if(tournaments==null){
            return false;
        }else{
            return true;
        }
    }

}






