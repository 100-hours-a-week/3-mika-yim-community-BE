package com.mika.ktdcloud.community.controller;

import com.mika.ktdcloud.community.dto.user.request.LoginRequest;
import com.mika.ktdcloud.community.dto.user.request.UserPasswordUpdateRequest;
import com.mika.ktdcloud.community.dto.user.request.UserCreateRequest;
import com.mika.ktdcloud.community.dto.user.request.UserUpdateRequest;
import com.mika.ktdcloud.community.dto.user.response.UserResponse;
import com.mika.ktdcloud.community.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
// 이 클래스를 REST API 엔드포인트로 등록
// Spring Application Context에서 빈으로 등록됨
// JSON이나 XML과 같은 다양한 형식으로 데이터를 반환 가능
@RequestMapping("/api/v1/users")
// 모든 메서드의 기본 경로를 /api/v1/users로 묶음
// 나중에 프론트엔드에서 HTML을 요청할 때 경로가 겹치지 않도록 /api 하위에 추가
@RequiredArgsConstructor
// 초기화 되지 않은 final 필드나, @NonNull이 붙은 필드에 대해 생성자 주입 방식으로 자동 의존성 주입
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    // HTTP 메서드가 POST일 때 전달 받는 URL과 동일하면 이 메서드로 매핑
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
        // @RequestBody로 JSON 요청을 받음
        // UserService.createUser를 실행
        UserResponse response = userService.createUser(request);
        URI location = URI.create("/api/v1/users/" + response.getId());
        return ResponseEntity.created(location).body(response);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestBody @Valid LoginRequest request){
        UserResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }

    // 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        UserResponse response = userService.getUser(id);
        // @PathVariable로 경로의 id를 받아 해당 유저를 조회
        return ResponseEntity.ok(response);
    }

    // 회원 수정
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse response = userService.updateUser(id, request);
        // @RequestBody의 변경 요청을 받아서 서비스에서 수정
        return ResponseEntity.ok(response);
    }

    // 비밀번호 변경
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody @Valid UserPasswordUpdateRequest request) {
        userService.updatePasswordUser(id, request);
        return ResponseEntity.ok().build();
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // JWT라면 클라이언트가 토큰을 버림
        // 세션 기반이라면 SecurityContextLogoutHandler 등 사용
        return ResponseEntity.ok().build();
    }

    // 회원 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        // 서비스의 delete(id)를 호출해 해당 유저의 soft delete를 진행한다.
        return ResponseEntity.noContent().build();
    }
}
