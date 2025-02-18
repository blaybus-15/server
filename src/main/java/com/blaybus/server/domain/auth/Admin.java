package com.blaybus.server.domain.auth;

import com.blaybus.server.domain.Center;
import com.blaybus.server.dto.request.MyPageRequest.AdminUpdateRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends Member {

    @Column(name = "name", nullable = false)
    private String name; // 이름

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "center_id")
    private Center center; // 센터 정보

    @Column(name = "contact_number", nullable = false)
    private String contactNumber; // 연락처

    @Column(name = "introduction")
    private String introduction; // 한줄 소개

    @Column(name = "profile_picture_url")
    private String profilePictureUrl; // 프로필 사진 (없으면 기본 아이콘)

    public Admin(String email, String password, LoginType loginType, String name,
                 Center center, String contactNumber,
                 String introduction, String profilePictureUrl) {
        super(email, password, Set.of(MemberRole.ADMIN), loginType);
        this.name = name;
        this.center = center;
        this.contactNumber = contactNumber;
        this.introduction = introduction;
        this.profilePictureUrl = profilePictureUrl;
    }

    // TODO: 비밀번호 확인.
    public void updateAdmin(Center center, AdminUpdateRequest request, String profilePictureUrl) {
        this.name = request.getName();
        this.center = center;
        this.contactNumber = request.getContactNumber();
        this.introduction = request.getIntroduction();
        this.profilePictureUrl = profilePictureUrl;
    }

    public void saveBySocial(Center center, String name, String contactNumber, String introduction, String profilePictureUrl) {
        this.center = center;
        this.name = name;
        this.contactNumber = contactNumber;
        this.introduction = introduction;
        this.profilePictureUrl = profilePictureUrl;
    }
}
