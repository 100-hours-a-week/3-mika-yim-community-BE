package com.mika.ktdcloud.community.service;

import com.mika.ktdcloud.community.dto.user.request.LoginRequest;
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

import java.util.List;

@Service
@RequiredArgsConstructor
// 초기화 되지 않은 final 필드나, @NonNull이 붙은 필드에 대해 생성자 주입 방식으로 자동 의존성 주입
@Transactional(readOnly = true)
// 이 클래스의 메서드가 실행될 때 DB 데이터를 읽기만 할 뿐, 절대 수정하지 않음
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Transactional // DB에 쓰기 작업이므로 readOnly 해제 오버라이딩
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

        String encodedPassword = passwordEncoder.encode(request.getPassword()); // .encode() 메서드로 비밀번호 암호화

        User user = userMapper.toEntity(request, encodedPassword); // 매퍼를 사용해 객체를 생성
        User savedUser = userRepository.save(user); // INSERT, 메서드에 쓰기 트랜잭션이 있어 커밋 시 DB에 반영

        return userMapper.toResponse(savedUser); // 다시 응답으로 변환하여 반환
    }

    // 로그인
    public UserResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email not found."));
        if (user.getDeletedAt() != null) {
            throw new IllegalArgumentException("Deleted User.");
        }
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // DTO로 받은 평문 비밀번호와 DB의 암호화된 비밀번호 비교
            // matches() 메서드가 내부적으로 salt를 고려하여 일치 여부를 확인해준다.
            throw new IllegalArgumentException("Incorrect password.");
        }

        // 로그인 성공, 이후 JWT 토큰 생성 등 로직 추가해야 됨
        return userMapper.toResponse(user);
    }

    // 회원 조회
    public UserResponse getUser(Long id) {
        User user = userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        // Optional 반환, 없을 때는 404/검증 실패 흐름에 맞춰 예외 던짐
        // 엔티티의 데이터가 실제로 필요할 때 사용

        return userMapper.toResponse(user);
    }

    public User getReferenceById(Long id){
        return userRepository.getReferenceById(id);
        // 프록시(지연 로딩용 레퍼런스)를 반환, 실제 필드 접근 시에만 SELECT 쿼리 실행
        // 다른 엔티티와의 연관 관계만 설정할 때, 성능 최적화 용도로 사용
        // 예를 들어 Post의 외래키를 설정할 때 사용
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
