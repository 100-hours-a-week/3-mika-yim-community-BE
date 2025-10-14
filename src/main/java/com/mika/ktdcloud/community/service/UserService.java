package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.user.request.UserPasswordUpdateRequest;
import com.mika.ktdcloud.community.dto.user.request.UserCreateRequest;
import com.mika.ktdcloud.community.dto.user.request.UserUpdateRequest;
import com.mika.ktdcloud.community.dto.user.response.UserResponse;
import com.mika.ktdcloud.community.entity.User;
import com.mika.ktdcloud.community.mapper.UserMapper;
import com.mika.ktdcloud.community.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Transactional
    public UserResponse createUser(UserCreateRequest request){
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("This email is already in use.");
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("This nickname is already in use.");
        }
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("Password does not match.");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = userMapper.toEntity(request, encodedPassword);
        User savedUser = userRepository.save(user);

        return userMapper.toResponse(savedUser);
    }

    // id로 회원 조회
    @Transactional(readOnly = true)
    public UserResponse getUser(Long id) {
        // Optional 반환, 엔티티의 데이터가 실제로 필요할 때 사용
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        return userMapper.toResponse(user);
    }

    // id로 회원 레퍼런스 조회
    @Transactional(readOnly = true)
    public User getReferenceById(Long id){
        // 프록시(지연 로딩용 레퍼런스)를 반환, 실제 필드 접근 시에만 SELECT 쿼리 실행
        return userRepository.getReferenceById(id);
    }

    // 회원 수정
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            if (userRepository.existsByNickname(request.getNickname())) {
                throw new IllegalArgumentException("Nickname is already in use.");
            }
        }
        user.updateProfile(request.getNickname(), request.getProfileImageUrl());
        return userMapper.toResponse(user);
    }

    // 비밀번호 수정
    @Transactional
    public void updatePasswordUser(Long id, UserPasswordUpdateRequest request) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password.");
        }
        if (!request.getNewPassword().equals(request.getNewPasswordConfirm())) {
            throw new IllegalArgumentException("The new password does not match.");
        }
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.updatePassword(encodedNewPassword);
    }

    // 회원 삭제
    @Transactional
    public void deleteUser(Long id){
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                        .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.softDelete();
    }
}
