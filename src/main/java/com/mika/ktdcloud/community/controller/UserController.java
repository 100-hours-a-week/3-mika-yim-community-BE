package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.user.request.UserPasswordUpdateRequest;
import com.mika.ktdcloud.community.dto.user.request.UserCreateRequest;
import com.mika.ktdcloud.community.dto.user.request.UserUpdateRequest;
import com.mika.ktdcloud.community.dto.user.response.UserResponse;
import com.mika.ktdcloud.community.service.UserService;
import com.mika.ktdcloud.community.util.SecurityUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        UserResponse response = userService.createUser(request);
        URI location = URI.create("/api/v1/users/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse response = userService.getUser(id);
        return ResponseEntity.ok(response);
    }

    // 현재 사용자 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyProfile() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        UserResponse response = userService.getUser(currentUserId);
        return ResponseEntity.ok(response);
    }

    // 현재 사용자 회원 수정
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyProfile(@RequestBody @Valid UserUpdateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        UserResponse response = userService.updateUser(currentUserId, request);
        return ResponseEntity.ok(response);
    }

    // 회원 수정
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    // 현재 사용자 비밀번호 변경
    @PatchMapping("/me/password")
    public ResponseEntity<Void> updatePassword(@RequestBody @Valid UserPasswordUpdateRequest request) {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        userService.updatePasswordUser(currentUserId, request);
        return ResponseEntity.ok().build();
    }

    // 비밀번호 변경
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody @Valid UserPasswordUpdateRequest request) {
        userService.updatePasswordUser(id, request);
        return ResponseEntity.ok().build();
    }

    // 현재 회원 삭제
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        Long currentUserId = SecurityUtil.getCurrentUserId();
        userService.deleteUser(currentUserId);
        // 서비스의 delete(id)를 호출해 해당 유저의 soft delete를 진행한다.
        return ResponseEntity.noContent().build();
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        // 서비스의 delete(id)를 호출해 해당 유저의 soft delete를 진행한다.
        return ResponseEntity.noContent().build();
    }
}
