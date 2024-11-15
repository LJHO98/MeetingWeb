package com.MeetingWeb.Service;

import com.MeetingWeb.Dto.GroupCategoryDto;
import com.MeetingWeb.Dto.UserDto;
import com.MeetingWeb.Dto.UserProfileDto;
import com.MeetingWeb.Entity.GroupCategory;
import com.MeetingWeb.Entity.User;
import com.MeetingWeb.Entity.UserSelectCategory;
import com.MeetingWeb.Repository.GroupCategoryRepository;
import com.MeetingWeb.Repository.GroupRepository;
import com.MeetingWeb.Repository.UserRepository;
import com.MeetingWeb.Repository.UserSelectCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final GroupCategoryRepository groupCategoryRepository;
    private final ProfileUploadService profileUploadService;
    private final GroupRepository groupRepository;
    private final UserSelectCategoryRepository userSelectCategoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${userProfileImgPath}")
    private String userProfileImgPath;



    public List<GroupCategoryDto> getGroupCategories() {
        List<GroupCategory> categories = groupCategoryRepository.findAllByOrderByGroupCategoryIdAsc();
        return categories.stream()
                .map(GroupCategoryDto::of)  // of 메서드로 변환
                .collect(Collectors.toList());
    }



    public User findByUserName(String userName) {
        User user = userRepository.findByUserName(userName);
        return user;
    }
    public void findByUserId(Long userId) {
        // Optional을 사용하여 존재 여부를 확인한 후 User 객체를 반환
       userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 유저가 존재하지 않습니다. ID: " + userId));
    }

    public void singUp(UserDto userDto, PasswordEncoder passwordEncoder) {
        try {
            String profileImageUrl = profileUploadService.saveProfile(userDto.getProfileImage());
            //DB에서 모든 GroupCategory 가져오기
            List<GroupCategoryDto> groupCategories = getGroupCategories();
            //User 엔티티 생성
            User user = userDto.toEntity(groupCategories, passwordEncoder, profileImageUrl);
            userRepository.save(user);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //member 테이블에 이메일 존재여부 확인
    public void isExistEmail(String email) {
        User find = userRepository.findByEmail(email);
        if( find == null){
            throw new IllegalArgumentException("존재하지 않는 이메일입니다. 다시 입력하세요.");
        }
    }

    public User findByEmail(String email) {
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("이메일은 null이거나 빈 값일 수 없습니다.");
        }


        return userRepository.findByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUserName())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRole().toString()))
                .build();
    }

    public boolean isUserNameTaken(@Pattern(regexp = "^[a-zA-Z0-9]*$", message = "아이디는 영어, 숫자만 사용 가능합니다.") @Size(min = 4, max = 10, message = "아이디는 4~10자입니다.") String userName) {
        return userRepository.findByUserName(userName) != null;
    }

    public List<GroupCategory> getUserSelectedCategories(String userName) {
        //// 유저가 선택한 카테고리를 가져와 리스트로 반환하는 메서드입니다.
        User user = userRepository.findByUserName(userName);
        return user.getSelectedCategories()
                .stream()
                .map(UserSelectCategory::getGroupCategory)
                .collect(Collectors.toList());
    }

    //사용자 정보 조회
    public UserProfileDto getUserProfile(String userName) {
        // 사용자 이름으로 사용자 정보를 조회하고 DTO로 변환
        User user = userRepository.findByUserName(userName);
        if (user == null) {
            throw new IllegalArgumentException("유저정보 조회 실패");
        }
        return UserProfileDto.of(user);
    }


    public void updateUserProfile(UserProfileDto userProfileDto) throws IOException {
        // 사용자 이름을 통해 사용자 찾기
        User user = userRepository.findByUserName(userProfileDto.getUserName());
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // 프로필 이미지 업데이트 처리
        profileImageUpdate(userProfileDto, user);

        // DTO 필드를 User 엔티티에 적용
        userProfileDto.updateEntity(user);

        // 선택한 카테고리 업데이트
        updateUserCategories(userProfileDto, user);

        // 변경된 사용자 정보 저장
        userRepository.save(user);
    }

    /**
     * 프로필 이미지 업데이트 처리 메서드.
     * 새로운 이미지가 있을 경우 기존 이미지를 삭제하고 새로운 이미지를 저장합니다.
     */
    private void profileImageUpdate(UserProfileDto userProfileDto, User user) throws IOException {
        // 새 프로필 이미지가 있는 경우에만 처리
        if (userProfileDto.getProfileImg() != null && !userProfileDto.getProfileImg().isEmpty()) {
            // 기존 프로필 이미지 삭제
            deleteExistingProfileImage(user.getProfileImgUrl());

            // 새 이미지 저장 및 URL 설정
            String newProfileImgUrl = profileUploadService.saveProfile(userProfileDto.getProfileImg());
            userProfileDto.setProfileImgUrl(newProfileImgUrl);
        }
    }

    /**
     * 기존 프로필 이미지 삭제 메서드.
     * 이미지 URL을 통해 파일 시스템에서 삭제를 시도합니다.
     */
    private void deleteExistingProfileImage(String profileImgUrl) {
        if (profileImgUrl != null) {
            // 파일 경로 생성
            String existingFilePath = userProfileImgPath + profileImgUrl.replaceFirst("/img", "");
            File existingFile = new File(existingFilePath);

            // 파일이 존재하면 삭제
            if (existingFile.exists()) {
                existingFile.delete();
            }
        }
    }

    /**
     * 사용자 선택 카테고리 업데이트 메서드.
     * 기존 카테고리와 새로운 카테고리를 비교하여 추가 및 삭제를 수행합니다.
     */
    private void updateUserCategories(UserProfileDto userProfileDto, User user) {
        // 현재 유저가 선택한 기존 카테고리 ID 목록
        List<Long> existingCategoryIds = user.getSelectedCategories().stream()
                .map(usc -> usc.getGroupCategory().getGroupCategoryId())
                .collect(Collectors.toList());

        // 새로운 선택된 카테고리 ID 목록
        List<Long> newCategoryIds = userProfileDto.getSelectedCategoryIds();

        // 기존 카테고리에서 제거할 항목과 추가할 항목 처리
        removeCategories(user, existingCategoryIds, newCategoryIds);
        addCategories(user, existingCategoryIds, newCategoryIds);
    }

    /**
     * 카테고리 제거 메서드.
     * 기존 카테고리 중 새로운 목록에 없는 항목을 삭제합니다.
     */
    private void removeCategories(User user, List<Long> existingCategoryIds, List<Long> newCategoryIds) {
        // 삭제할 카테고리 목록 추출
        List<UserSelectCategory> categoriesToRemove = user.getSelectedCategories().stream()
                .filter(usc -> !newCategoryIds.contains(usc.getGroupCategory().getGroupCategoryId()))
                .collect(Collectors.toList());

        // 기존 컬렉션에서 삭제할 항목 제거 및 DB에서 삭제
        categoriesToRemove.forEach(user.getSelectedCategories()::remove);
        userSelectCategoryRepository.deleteAll(categoriesToRemove);
    }

    /**
     * 카테고리 추가 메서드.
     * 새로운 선택된 카테고리 목록에 있지만 기존에 없는 항목을 추가합니다.
     */
    private void addCategories(User user, List<Long> existingCategoryIds, List<Long> newCategoryIds) {
        // 추가할 카테고리 목록 추출 및 추가
        newCategoryIds.stream()
                .filter(id -> !existingCategoryIds.contains(id))
                .forEach(categoryId -> {
                    GroupCategory category = groupCategoryRepository.findById(categoryId)
                            .orElseThrow(() -> new IllegalArgumentException("Category not found"));

                    // 새로운 UserSelectCategory 생성 후 추가
                    UserSelectCategory userSelectCategory = new UserSelectCategory();
                    userSelectCategory.setUser(user);
                    userSelectCategory.setGroupCategory(category);
                    user.getSelectedCategories().add(userSelectCategory);
                });
    }


//    public User findByPassword(String password) {
//        User user = userRepository.findByPassword(password);
//        return user;
//    }

    //비밀번호 변경
    public boolean changePassword(String pw, String email) {
        // 비밀번호 암호화
        System.out.println("dddd"+pw);
        String encodedPassword = passwordEncoder.encode(pw);

        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setPassword(encodedPassword); // 암호화된 비밀번호로 변경
            userRepository.save(user); // 변경된 비밀번호를 저장
            return true;
        }
        return false;
    }

    public void deleteUser(Long id){



        userRepository.deleteById(id);


    }

}

