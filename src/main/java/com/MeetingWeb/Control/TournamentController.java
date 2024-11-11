package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.*;
import com.MeetingWeb.Entity.TournamentCategory;
import com.MeetingWeb.Entity.Tournaments;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Repository.TournamentParticipantRepository;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.TournamentService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class TournamentController {
    private final TournamentService tournamentService;
    private final UserService userService;
    private final GroupService groupService;
    private final TournamentParticipantRepository tournamentParticipantRepository;


    //대회 목록
    @GetMapping("/tournament/list")
    public String tournamentListPage(Model model) {
        List<TrnDto> TournamentList = tournamentService.getTournamentList();
        model.addAttribute("tournamentList", TournamentList);
        model.addAttribute("categories",tournamentService.getTournamentCategories());
        return "/tournament/tournamentList";
    }

    // 대회 검색 처리
    @GetMapping("/tournament/search")
    public String searchTournaments(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "inputText", required = false) String inputText,
            Model model) {

        List<TrnDto> tournamentList = tournamentService.searchTournament(categoryId, inputText);

        model.addAttribute("tournamentList", tournamentList);
        model.addAttribute("categories",tournamentService.getTournamentCategories());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("inputText", inputText);

        return "tournament/tournamentList";
    }

    // 대회 생성 폼을 보여주는 메서드
    @GetMapping("/tournament/createTournament")
    public String createTournamentForm(Model model) {
        model.addAttribute("trnDto", new TrnDto());
        model.addAttribute("categories",tournamentService.getTournamentCategories());
        return "tournament/createTournament";
    }

    // 대회 생성 요청 처리 메서드
    @PostMapping("/tournament/createTournament")
    public String createTournament(@Valid TrnDto trnDto, Principal principal, RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User createdBy = userService.findByUserName(username);
            
        // User 서비스에서 User 객체 조회
        try {
            // 대회 생성 호출
            tournamentService.createTournament(trnDto,createdBy);
        } catch (SecurityException e) {
            // 권한 부족 시 메시지 추가하고 /home으로 리다이렉트
            redirectAttributes.addFlashAttribute("errorMessage", "대회를 생성할 권한이 없습니다.");
            return "redirect:/home";
        } catch (Exception e) {
            // 다른 예외 처리 (필요 시)
            redirectAttributes.addFlashAttribute("errorMessage", "대회 생성 중 오류가 발생했습니다.");
            return "redirect:/home";
        }
        //return "redirect:/tournament/list"; // 성공 시 대회 목록으로 이동
        return "redirect:/home";
    }

    //대회 상세 페이지
    @GetMapping("/tournament/{tournamentId}")
    public String tournamentInfo(@PathVariable Long tournamentId , Model model, Principal principal) {
        TrnDto trnDto = tournamentService.getTournamentInfo(tournamentId);
        GroupProfileDto groupProfileDto = groupService.getGroupProfile(trnDto.getCreatedBy());
        List<GroupDto> groupDtoList = tournamentService.getMyGroupList(principal.getName());
        model.addAttribute("groupList", groupDtoList);
        model.addAttribute("tournament", trnDto);
        model.addAttribute("groupProfileDto", groupProfileDto);
        return "tournament/tournamentInfo";
    }

    //대회 참가 신청
    @PostMapping("/tournament/apply")
    public String applyTournament(@RequestParam Long tournamentId, @RequestParam Long groupId) {
        // 서비스 메서드를 호출하여 대회 신청 처리
        tournamentService.applyTournament(tournamentId, groupId);
        return "redirect:/tournament/" + tournamentId; // 신청 후 상세 페이지로 리다이렉트
    }

    //참가 모임
    @GetMapping("/tournament/{tournamentId}/participants")
    public String getTournamentParticipants(@PathVariable Long tournamentId, Model model, Principal principal) {
        // 서비스에서 참가자 리스트 가져오기
        List<TournamentParticipantDto> participantList = tournamentService.getParticipants(tournamentId);
        // 뷰 페이지에 전달
        model.addAttribute("participantList", participantList);
        TrnDto trnDto = tournamentService.getTournamentInfo(tournamentId);
        GroupProfileDto groupProfileDto = groupService.getGroupProfile(trnDto.getCreatedBy());
        List<GroupDto> groupDtoList = tournamentService.getMyGroupList(principal.getName());
        model.addAttribute("groupList", groupDtoList);
        model.addAttribute("tournament", trnDto);
        model.addAttribute("groupProfileDto", groupProfileDto);
        return "tournament/participantList"; // 뷰 템플릿의 경로에 맞게 수정
    }
    
    //대회 정보 업데이트
    public String updateTournament(TrnDto trnDto, Model model) {

        return "";
    }
    //내 대회
    @GetMapping("/tournament/myTournament")
    public String myTournamentPage(Principal principal, Model model) {
        List<TrnDto> myTournament = tournamentService.getMyTournament(principal.getName());
        List<TrnDto> myGroupTournament = tournamentService.getMyGroupTournament(principal.getName());

        if (myTournament.isEmpty()) {
            model.addAttribute("myTournamentMessage", "내가 만든 대회가 없습니다.");
        } else {
            model.addAttribute("myTournament", myTournament);
        }

        if (myGroupTournament.isEmpty()) {
            model.addAttribute("myGroupTournamentMessage", "내가 가입한 모임이 참가하는 대회가 없습니다.");
        } else {
            model.addAttribute("myGroupTournament", myGroupTournament);
        }

        return "tournament/myTournament";
    }
    //대진표 페이지 맵핑
    @GetMapping("/tournament/bracket/{tournamentId}")
    public String tournamentGraph(@PathVariable Long tournamentId,Model model) {
        boolean isOk = tournamentService.isOkCreateBracket(tournamentId);

        if(isOk) {
            TrnDto trnDto = tournamentService.getTournamentInfo(tournamentId);
            model.addAttribute("trnDto", trnDto);
            List<TournamentParticipantDto> groupList = tournamentService.getParticipantList(tournamentId);
            model.addAttribute("groupList", groupList);
        }else{
            return "redirect:/tournament/" + tournamentId;
        }
        return "tournament/tournamentBracket";
    }
    //대회 대진표 섞기
    @GetMapping("/match")
    public String random(@RequestParam("tournamentId") Long tournamentId, Model model) {
        if(!tournamentService.isStartTournament(tournamentId)) {
            tournamentService.shuffle(tournamentId);
        }else{
            throw new IllegalArgumentException("대회 중에는 셔플 금지");
        }

        return"redirect:/tournament/bracket/"+tournamentId;
    }

    @GetMapping("/tournament/matchResult")
    public String macthResult(@RequestParam("wgid") Long winId, @RequestParam("tid") Long tournamentId,
                              @RequestParam("score") int score, @RequestParam("matchNum") int matchNumber, Model model) {
//        if(tournamentService.isStartTournament(tournamentId)) {
            tournamentService.selectResult(winId, tournamentId, score, matchNumber);
//        }else{
//            throw new IllegalArgumentException("대회 시작전입니다.");
//        }
        return "redirect:/tournament/bracket/"+tournamentId;
    }

    @GetMapping("/shuffle")
    public String shuffle(@RequestParam("tournamentId") Long tournamentId, Model model) {
        tournamentService.shuffle(tournamentId);
        return"redirect:/tournament/bracket/"+tournamentId;
    }


}
