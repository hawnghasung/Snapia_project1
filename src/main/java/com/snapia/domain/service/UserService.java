package com.snapia.domain.service;

import com.snapia.domain.constant.Role;
import com.snapia.domain.dto.BusinessProfileDTO;
import com.snapia.domain.dto.ProfileDTO;
import com.snapia.domain.dto.ProfilePostDTO;
import com.snapia.domain.dto.UserDTO;
import com.snapia.domain.entity.BusinessProfile;
import com.snapia.domain.entity.Post;
import com.snapia.domain.entity.Users;
import com.snapia.domain.repository.BusinessProfileRepository;
import com.snapia.domain.repository.PostRepository;
import com.snapia.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileService fileService;
    private final PostRepository postRepository;
    private final LikesService likesService;
    private final CommentService commentService;
    private final BusinessProfileRepository businessProfileRepository;

    public void signUp(UserDTO userDTO, BusinessProfileDTO businessProfileDTO) {
        Users users = Users.builder()
                .snapiaId(userDTO.getSnapiaId())
                .email(userDTO.getEmail())
                .name(userDTO.getName())
                .phoneNum(userDTO.getPhoneNum())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .role(Role.USER)
                .isBusiness(userDTO.isBusiness())
                .gender(userDTO.getGender())
                .profileImageUrl(userDTO.getProfileImageUrl())
                .pubOrNot(userDTO.getPubOrNot())
                .build();


        userRepository.save(users);

        if (users.isBusiness()) {
            BusinessProfile businessProfile = BusinessProfile.builder()
                    .contact(businessProfileDTO.getContact())
                    .category(businessProfileDTO.getCategory())
                    .companyName(businessProfileDTO.getCompanyName())
                    .operatingHours(businessProfileDTO.getOperatingHours())
                    .introduce(businessProfileDTO.getIntroduce())
                    .noticeMessage(businessProfileDTO.getNoticeMessage())
                    .users(users)
                    .build();

            businessProfileRepository.save(businessProfile);

            users.setBusinessProfile(businessProfile);

        }
    }



    public ProfileDTO profileUpdate(ProfileDTO profileDTO,BusinessProfileDTO businessProfileDTO,Long uid) {
        Users users = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 1. 아이디 중복 체크
        if (!users.getSnapiaId().equals(profileDTO.getSnapiaId())) {
            if (userRepository.existsBySnapiaId(profileDTO.getSnapiaId())) {
                throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
            }
            users.setSnapiaId(profileDTO.getSnapiaId());
        }


        // 2. 사진 파일 처리
        MultipartFile file = profileDTO.getMultipartFile();
        if (file != null && !file.isEmpty()) {
            String fileName = fileService.saveFile(file);
            users.setProfileImageUrl(fileName);
        }

        // 3. 나머지 정보 업데이트
        users.setBio(profileDTO.getBio());
        users.setGender(profileDTO.getGender());

        // 추가: name, email 업데이트 (null이 아닐 때만)
        if (profileDTO.getName() != null) {
            users.setName(profileDTO.getName());
        }
        if (profileDTO.getEmail() != null) {
            users.setEmail(profileDTO.getEmail());
        }

        if (users.isBusiness()) {
            BusinessProfile businessProfile = users.getBusinessProfile();
            // 기존 프로필 수정
            businessProfile.setContact(businessProfileDTO.getContact());
            businessProfile.setCategory(businessProfileDTO.getCategory());
            businessProfile.setCompanyName(businessProfileDTO.getCompanyName());
            businessProfile.setOperatingHours(businessProfileDTO.getOperatingHours());
            businessProfile.setIntroduce(businessProfileDTO.getIntroduce());
            businessProfile.setNoticeMessage(businessProfileDTO.getNoticeMessage());

            users.setBusinessProfile(businessProfile);
        }

        return ProfileDTO.builder()
                .name(users.getName())
                .profileImageUrl(users.getProfileImageUrl())
                .snapiaId(users.getSnapiaId())
                .password(users.getPassword())
                .email(users.getEmail())
                .bio(users.getBio())
                .gender(users.getGender())
                .businessProfile_id(users.isBusiness() ? users.getBusinessProfile().getBpid() : null)
                .build();
    }

    public List<ProfilePostDTO> getProfilePosts(Long userId) {
        List<Post> posts = postRepository.findByUsersUid(userId);

        return posts.stream().map(post ->
                ProfilePostDTO.builder()
                        .pid(post.getPid())
                        .postImgUrl(post.getPostImgUrl())
                        .likeCount(likesService.getLikeCount(post.getPid()).intValue())
                        .commentCount(commentService.getCommentCount(post.getPid()).intValue())
                        .mediaType(post.getMediaType())       // ✅ 추가
                        .thumbnailUrl(post.getThumbnailUrl())
                        .build()
        ).collect(Collectors.toList());
    }

    public Users findBySnapiaId(String snapiaId) {
        return (Users) userRepository.findBySnapiaId(snapiaId)
                .orElseThrow(()->new IllegalArgumentException("찾기 불가"));
    }

}
