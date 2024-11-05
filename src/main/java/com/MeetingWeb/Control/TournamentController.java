package com.MeetingWeb.Control;

import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Dto.TournamentSearchDto;
import com.MeetingWeb.Dto.TrnDto;
import com.MeetingWeb.Entity.Groups;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Service.GroupService;
import com.MeetingWeb.Service.TournamentService;
import com.MeetingWeb.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;

    @Controller
    @RequiredArgsConstructor
    public class TournamentController {
        private final TournamentService tournamentService;
        private final UserService userService;
        private final GroupService groupService;

        private List<GroupDto> groupList;

        //대회 검색 페이지, 검색 값이 있다면 검색결과에 맞는 결과 보여주기 없다면 default 목록 보여주기

        public String searchTournament(TournamentSearchDto tournamentSearchDto, Model model) {


            return "";
        }
        //대회 목록
        public String tournamentListPage(Model model) {

            return "";
        }
        //대회 만들기 폼
        // 대회 생성 폼을 보여주는 메서드
        @GetMapping("/tournament/createTournament")
        public String createTournamentForm(Model model) {
            model.addAttribute("trnDto", new TrnDto());
            model.addAttribute("categories",tournamentService.getTournamentCategories());
            return "tournament/createTournament";
        }

        // 대회 생성 요청 처리 메서드
        @PostMapping("/tournament/createTournament")
        public String createTournament(TrnDto trnDto, Principal principal, RedirectAttributes redirectAttributes) {
            String username = principal.getName();
            User createdBy = userService.findByUserName(username); // User 서비스에서 User 객체 조회
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
        //    //대회 만들기
//            @PostMapping("/home")
//            public String createTournament(@ModelAttribute TrnDto trnDto,User createdBy) throws Exception {
//
//                tournamentService.createTournament(trnDto,createdBy);
//
//                return "home";
//
//            }

//        private String saveFile(MultipartFile file) {
//            // 저장할 디렉토리 경로 설정 (예: "uploads" 디렉토리)
//            String uploadDir = "uploads/";
//
//            // 디렉토리가 없으면 생성
//            File directory = new File(uploadDir);
//            if (!directory.exists()) {
//                directory.mkdirs();
//            }
//
//            // 파일 이름 중복 방지를 위해 고유한 파일 이름 생성
//            String originalFileName = file.getOriginalFilename();
//            String uniqueFileName = System.currentTimeMillis() + "_" + originalFileName;
//
//            // 파일 저장 경로 설정
//            Path filePath = Paths.get(uploadDir, uniqueFileName);
//            try {
//                // 파일 저장
//                Files.write(filePath, file.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//                throw new RuntimeException("파일 저장에 실패했습니다: " + originalFileName);
//            }
//
//            // 저장된 파일 경로를 반환
//            return filePath.toString();
//        }




        //대회 상세 페이지
        public String tournamentInfo(Principal principal,Long tournamentId , Model model) {
            return "";
        }
        //대회 정보 업데이트
        public String updateTournament(TrnDto trnDto, Model model) {

            return "";
        }
        //내 대회
        public String myTournamentPage(Principal principal ,Model model) {
            return "";
        }
        //대진표 페이지 맵핑
        public String tournamentGraph(Long tournamentId,Model model) {
            return "";

        }
        //참가모임페이지 맵핑
        public String joinGather(Long tournamentId,Model model) {
            return "";

        }


    }
