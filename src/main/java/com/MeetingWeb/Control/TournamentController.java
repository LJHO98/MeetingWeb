package com.MeetingWeb.Control;

import com.MeetingWeb.Constant.TournamentStatus;
import com.MeetingWeb.Dto.*;
import com.MeetingWeb.Repository.TournamentParticipantRepository;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.TournamentService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

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
    public String createTournamentForm(Model model, Principal principal, RedirectAttributes redirectAttributes) {
        if(!tournamentService.isGroupOwner(principal.getName())){
            redirectAttributes.addFlashAttribute("errorMsg", "모임장만 대회 생성이 가능합니다. 모임을 먼저 만들어주세요.");
            return "redirect:/tournament/list";
        }
        List<GroupDto> groupList = tournamentService.getMyGroupList(principal.getName());
        model.addAttribute("groupList" ,groupList);
        model.addAttribute("trnDto", new TrnDto());
        model.addAttribute("categories",tournamentService.getTournamentCategories());
        return "tournament/createTournament";
    }

    // 대회 생성 요청 처리 메서드
    @PostMapping("/tournament/createTournament")
    public String createTournament(@Valid TrnDto trnDto, BindingResult bindingResult ,Principal principal, Model model, RedirectAttributes redirectAttributes) {
        String userName = principal.getName();
        if(bindingResult.hasErrors()){
            List<GroupDto> groupDtoList = tournamentService.getMyGroupList(principal.getName());
            model.addAttribute("groupList", groupDtoList);
            model.addAttribute("categories", tournamentService.getTournamentCategories());
            return "tournament/createTournament";
        }
        System.out.println("컨트롤 if문 밖에 : "+trnDto.getTournamentId());
        if(trnDto.getTournamentId()!=null){
            if(tournamentService.isExistTournament(trnDto.getTournamentId())){
                tournamentService.updateTournament(trnDto, trnDto.getCreatedBy());
                System.out.println("컨트롤 : "+trnDto.getTournamentId());
            }
        }else {
            try {
                // 대회 생성 호출
                tournamentService.createTournament(trnDto, userName);
            } catch (EntityNotFoundException e) {
                // 권한 부족 시 메시지
                redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
                return "redirect: /tournament/createTournament";
            } catch (IllegalArgumentException e) {
                // 다른 예외 처리 (필요 시)
                model.addAttribute("errorMessage", "대회 생성 중 오류가 발생했습니다.");
                return "tournament/createTournament";
            } catch (IOException e) {
                model.addAttribute("errorMessage", "대회 생성 중 오류가 발생했습니다.");
            }
        }

        return "redirect:/tournament/list"; // 성공 시 대회 목록으로 이동
    }

    //대회 정보 수정
    @GetMapping("/tournament/edit/{tournamentId}")
    public String updateTournament(@PathVariable Long tournamentId , Model model, Principal principal, RedirectAttributes redirectAttributes) {
        TrnDto trnDto = tournamentService.getTournamentInfo(tournamentId);
        System.out.println("수정페이지 : "+trnDto.getTournamentId());
        if(trnDto.getStatus() == TournamentStatus.UPCOMING) {
            List<GroupDto> groupList = tournamentService.getMyGroupList(principal.getName());
            model.addAttribute("groupList" ,groupList);
            model.addAttribute("trnDto", trnDto);
            model.addAttribute("categories",tournamentService.getTournamentCategories());
        }else{
            redirectAttributes.addFlashAttribute("errorMessage", "대회 상태가 수정할 수 없는 상태입니다.");
            return "redirect:/tournament/"+tournamentId;
        }
        return "tournament/createTournament";
    }


    //대회 상세 페이지
    @GetMapping("/tournament/{tournamentId}")
    public String tournamentInfo(@PathVariable Long tournamentId , Model model, Principal principal) {
        TrnDto trnDto = tournamentService.getTournamentInfo(tournamentId);
        GroupProfileDto groupProfileDto = groupService.getGroupProfile(trnDto.getGroupId());
        List<GroupDto> groupDtoList = tournamentService.getMyGroupList(principal.getName());
        model.addAttribute("groupList", groupDtoList);
        model.addAttribute("tournament", trnDto);
        model.addAttribute("groupProfileDto", groupProfileDto);
        model.addAttribute("isUserLeader", tournamentService.isUserLeader(tournamentId, principal.getName()));
        return "tournament/tournamentInfo";
    }

    //대회 참가 신청
    @PostMapping("/tournament/apply")
    public String applyTournament(@RequestParam Long tournamentId, @RequestParam Long groupId,RedirectAttributes redirectAttributes) {
        // 서비스 메서드를 호출하여 대회 신청 처리
        try {
            tournamentService.applyTournament(tournamentId, groupId);
        }catch (NonUniqueResultException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/tournament/" + tournamentId;
        }
        redirectAttributes.addFlashAttribute("errorMessage", "대회신청이 완료되었습니다.");
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
        GroupProfileDto groupProfileDto = groupService.getGroupProfile(trnDto.getGroupId());
        List<GroupDto> groupDtoList = tournamentService.getMyGroupList(principal.getName());
        model.addAttribute("groupList", groupDtoList);
        model.addAttribute("tournament", trnDto);
        model.addAttribute("groupProfileDto", groupProfileDto);
        model.addAttribute("isUserLeader", tournamentService.isUserLeader(tournamentId, principal.getName()));
        return "tournament/participantList"; // 뷰 템플릿의 경로에 맞게 수정
    }
    

    //내 대회
    @GetMapping("/tournament/myTournament")
    public String myTournamentPage(Principal principal, Model model) {
        List<TrnDto> myTournament = tournamentService.getMyTournament(principal.getName());
        List<TrnDto> myGroupTournament = tournamentService.getMyGroupTournament(principal.getName(),myTournament);

        if (myTournament.isEmpty()) {
            model.addAttribute("myTournamentMessage", "내가 만든 대회가 없습니다.");
        } else {
            model.addAttribute("myTournament", myTournament);
        }

        if (myGroupTournament.isEmpty()) {
            model.addAttribute("myGroupTournamentMessage", "내가 참가하는 대회가 없습니다.");
        } else {
            model.addAttribute("myGroupTournament", myGroupTournament);
        }

        return "tournament/myTournament";
    }
    //대진표 페이지 맵핑
    @GetMapping("/tournament/bracket/{tournamentId}")
    public String tournamentGraph(@PathVariable Long tournamentId, Model model, RedirectAttributes redirectAttributes, @AuthenticationPrincipal UserDetails userDetails) {
        boolean isOk = tournamentService.isOkCreateBracket(tournamentId);
        boolean isLeader = tournamentService.isUserLeader(tournamentId, userDetails.getUsername());

        if(isOk) {
            TrnDto trnDto = tournamentService.getTournamentInfo(tournamentId);
            model.addAttribute("trnDto", trnDto);
            List<TournamentParticipantDto> groupList = tournamentService.getParticipantList(tournamentId);
            model.addAttribute("groupList", groupList);
            model.addAttribute("isLeader", isLeader);
        }else{
            redirectAttributes.addFlashAttribute("errorMessage", "아직 모집이 끝나지 않아 대진표 열람이 불가합니다." );
            return "redirect:/tournament/" + tournamentId;
        }
        return "tournament/tournamentBracket";
    }
    //대회 대진표 섞기
    @GetMapping("/match")
    public String random(@RequestParam("tournamentId") Long tournamentId, RedirectAttributes redirectAttributes) {
        try {
            tournamentService.shuffle(tournamentId);
        }catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return"redirect:/tournament/bracket/"+tournamentId;
    }

//    @GetMapping("/tournament/matchResult")
//    public String macthResult(@RequestParam("wgid") Long winId, @RequestParam("tid") Long tournamentId,
//                              @RequestParam("scoreA") int scoreA, @RequestParam("matchNum") int matchNumber,
//                              @RequestParam("scoreB") int scoreB ,Model model) {
//        if(tournamentService.isStartTournament(tournamentId)) {
//            tournamentService.selectResult(winId, tournamentId, scoreA,scoreB, matchNumber);
//        }else{
//            throw new IllegalArgumentException("대회 시작전입니다.");
//        }
//        return "redirect:/tournament/bracket/"+tournamentId;
//    }

    @PostMapping("/tournament/matchResult")
    @ResponseBody // AJAX 요청에 JSON 응답을 반환
    public ResponseEntity<String> matchResult(
            @RequestParam("wgid") Long winId,
            @RequestParam("tid") Long tournamentId,
            @RequestParam("scoreA") int scoreA,
            @RequestParam("matchNum") int matchNumber,
            @RequestParam("scoreB") int scoreB) {
        try {
            tournamentService.selectResult(winId, tournamentId, scoreA, scoreB, matchNumber);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok("경기 결과가 성공적으로 저장되었습니다.");
    }



}
