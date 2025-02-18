package com.blaybus.server.service;

import com.blaybus.server.common.exception.CareLinkException;
import com.blaybus.server.common.exception.ErrorCode;
import com.blaybus.server.domain.auth.Admin;
import com.blaybus.server.domain.auth.CareGiver;
import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.auth.Experience;
import com.blaybus.server.dto.request.MyPageRequest.MemberUpdateRequest;
import com.blaybus.server.dto.request.MyPageRequest.AdminUpdateRequest;
import com.blaybus.server.repository.CenterRepository;
import com.blaybus.server.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MyPageWriteService {
    private final MemberRepository memberRepository;
    private final CenterRepository centerRepository;
    private final S3Service s3Service;

    // TODO: 업데이트 기획 후 수정
    public Long updateMemberInfo(Long memberId, MemberUpdateRequest memberUpdateRequest, MultipartFile file) {
        CareGiver careGiver = (CareGiver) memberRepository.findById(memberId)
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND));

        log.info("요양 보호사 조회 성공: {}", memberId);
        List<Experience> experiences = new ArrayList<>();
        if (memberUpdateRequest.getExperiences() != null) {
            experiences.addAll(
                    memberUpdateRequest.getExperiences().stream()
                            .map(exp -> {
                                Center center = centerRepository.findById(exp.getCenterId())
                                        .orElseThrow(() -> new CareLinkException(ErrorCode.CENTER_NOT_FOUND)); // 🚀 Center 조회

                                return new Experience(careGiver, center, exp.getCertificatedAt(), exp.getEndCertificatedAt(), exp.getAssignedTask());
                            })
                            .collect(Collectors.toList())
            );
        }

        if (careGiver.getProfilePictureUrl() != null) {
            s3Service.deleteFile(careGiver.getProfilePictureUrl());
        }

        String profileUrl = null;
        if (file != null && !file.isEmpty()) {
            profileUrl = s3Service.uploadFile(file);
        }

        careGiver.updateCareGiverInfo(memberUpdateRequest, experiences, profileUrl);
        memberRepository.save(careGiver);
        log.info("요양 보호사 수정 성공: {}", memberId);

        return careGiver.getId();
    }

    // TODO: 업데이트 기획 후 수정
    public Long updateAdminInfo(Long memberId, AdminUpdateRequest adminUpdateRequest, MultipartFile file) {
        Admin admin = (Admin) memberRepository.findById(memberId)
                .orElseThrow(() -> new CareLinkException(ErrorCode.USER_NOT_FOUND));
        log.info("관리자 조회 성공: {}", memberId);

        Center newCenter = centerRepository.findById(adminUpdateRequest.getCenterId())
                .orElseThrow(() -> new CareLinkException(ErrorCode.CENTER_NOT_FOUND));
        log.info("센터 조회 성공: {}", newCenter.getId());

        if (admin.getProfilePictureUrl() != null) {
            s3Service.deleteFile(admin.getProfilePictureUrl());
        }

        String profileUrl = null;
        if (file != null && !file.isEmpty()) {
            profileUrl = s3Service.uploadFile(file);
        }

        admin.updateAdmin(newCenter, adminUpdateRequest, profileUrl);
        memberRepository.save(admin);
        log.info("관리자 수정 성공: {}", memberId);

        return admin.getId();
    }
}
