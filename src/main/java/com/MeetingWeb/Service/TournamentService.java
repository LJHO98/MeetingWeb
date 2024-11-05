package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.GroupDto;
import com.MeetingWeb.Dto.TournamentCategoryDto;
import com.MeetingWeb.Dto.TournamentSearchDto;
import com.MeetingWeb.Dto.TrnDto;
import com.MeetingWeb.Entity.*;
import com.MeetingWeb.Repository.TournamentCategoryRepository;
import com.MeetingWeb.Repository.TournamentRepository;
import com.MeetingWeb.Repository.TournamentSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.swing.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
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

    //대회 카테고리 가져오기
    public List<TournamentCategoryDto> getTournamentCategories() {
        List<TournamentCategory> categories = tournamentCategoryRepository.findAllByOrderByTournamentCategoryIdAsc();
        return categories.stream()
                .map(TournamentCategoryDto::of)  // of 메서드로 변환
                .collect(Collectors.toList());
    }

    //카테고리 선택, 검색창에 입력한 값으로 대회 검색
    public List<TrnDto> searchTournament(TournamentSearchDto tournamentSearchDto) {
        return null;
    }


    //대회 목록 페이지에 보여줄 기본 대회 목록
    public  List<TrnDto> defaultTournament() {

        // Repository를 통해 모든 Tournaments 데이터를 조회
        List<Tournaments> tournamentsList = tournamentRepository.findAll();

        // TrnDto 리스트 생성
        List<TrnDto> trnDtoList = new ArrayList<>();

        // 각 Tournaments 객체를 TrnDto로 변환하여 리스트에 추가
        for (Tournaments tournament : tournamentsList) {
            TrnDto trnDto = TrnDto.of(tournament); // Tournaments 객체를 TrnDto로 변환
            trnDtoList.add(trnDto); // 변환된 TrnDto를 리스트에 추가
        }

        // 최종적으로 TrnDto 리스트 반환
        return trnDtoList;
    }
    //
    private boolean hasLeaderRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        // for 루프를 사용하여 권한 확인
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_LEADER")) {
                return true;  // ROLE_LEADER 권한이 있는 경우 true 반환
            }
        }
        return false;  // ROLE_LEADER 권한이 없는 경우 false 반환
    }

    //대회 만들기
    @Transactional
    public Tournaments createTournament(TrnDto trnDto, User createdBy)throws Exception {
        TournamentCategory tournamentCategory = tournamentCategoryRepository.findById(trnDto.getCategory())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + trnDto.getCategory()));

        String tournamentImgUrl = profileUploadService.saveProfile(trnDto.getTournamentImg());

        Tournaments tournament = trnDto.toEntity(tournamentImgUrl,createdBy, tournamentCategory);

        return tournamentRepository.save(tournament);

    }

}
