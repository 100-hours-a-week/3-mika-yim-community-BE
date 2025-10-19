package com.mika.ktdcloud.community.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자, 리플렉션과 프록시 객체 생성을 위해서 필요함
public class User extends AbstractAuditable{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true, length = 20)
    private String nickname;
    @Column(name = "profile_image_url", columnDefinition = "VARCHAR(255) DEFAULT 'default_profile_image'")
    private String profileImageUrl;

    @PrePersist
    public void prePersist() {
        if (this.profileImageUrl == null) {
            this.profileImageUrl = "/images/default-profile-image.png";
        }
    }

    public static User create(String email, String password, String nickname, String profileImageUrl) {
        User user = new User();
        user.email = email;
        user.password = password;
        user.nickname = nickname;
        user.profileImageUrl = profileImageUrl;
        return user;
    }

    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null && !nickname.equals(this.nickname)) {
            this.nickname = nickname;
        }
        if (profileImageUrl != null && !profileImageUrl.equals(this.profileImageUrl)) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void updatePassword(String encodedNewPassword) {
        this.password = encodedNewPassword;
    }

    @Override
    public void softDelete() {
        super.softDelete();
        // 현재는 소프트 딜리트 때문에 탈퇴하면 그 이메일을 영구적으로 재사용 불가능함
        // 30일 후 아카이빙 이후 재사용 허용하는 방법을 구현해야 함
    }

}
