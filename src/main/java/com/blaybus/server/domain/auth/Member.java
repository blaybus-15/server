package com.blaybus.server.domain.auth;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Member", indexes = {
        @Index(name = "idx_member_email", columnList = "email", unique = true),
})
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password")
    private String password;

    @ElementCollection(targetClass = MemberRole.class, fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<MemberRole> memberRoles;

    @Enumerated(EnumType.STRING)
    @Column(name = "login_type")
    private LoginType loginType;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    protected Member(String email, String password, Set<MemberRole> role,
                     LoginType loginType) {
        this.email = email;
        this.password = password;
        this.memberRoles = role;
        this.loginType = loginType;
    }
}
