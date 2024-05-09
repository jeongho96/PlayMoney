package com.reboot.playmoney.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Table(name = "member")
@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Member implements UserDetails {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_number", updatable = false)
    private Long memberNumber;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "username", unique = true)
    @Setter
    private String name;

    @Column(name = "social_provider")
    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider; // 추가된 부분

    public enum SocialProvider {
        GOOGLE,
        KAKAO,
        NAVER
    }


    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserType userType; // 추가된 부분

    public enum UserType {
        SELLER, USER
    }


    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "member")
    private List<WatchHistory> watchHistories = new ArrayList<>();

    @Builder
    public Member(String email, String password, String username, SocialProvider socialProvider, UserType userType) {
        this.email = email;
        this.password = password;
        this.name = username;
        this.socialProvider = socialProvider;
        this.userType = userType;
        this.createdAt = LocalDateTime.now();
    }

    public Member update(String username) {
        this.name = username;
        return this;
    }



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String toString() {
        return "User{" +
                "memberNumber=" + memberNumber +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", socialProvider=" + socialProvider +
                ", userType=" + userType +
                '}';
    }
}
