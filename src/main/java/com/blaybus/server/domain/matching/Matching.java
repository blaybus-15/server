package com.blaybus.server.domain.matching;

import com.blaybus.server.domain.senior.Senior;
import com.blaybus.server.domain.auth.CareGiver;
import com.blaybus.server.domain.Day;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Matching {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 매칭할 어르신과의 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    // 매칭 대상 요양보호사와의 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caregiver_id", nullable = false)
    private CareGiver caregiver;

    // 제안 임금 (예: 시급)
    @Column(nullable = false)
    private Integer wage;

    // 근무 요일
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Day> visitDays = new HashSet<>();

    // 근무 시간: 시작과 종료 (시:분 형식)
    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    // 매칭 요청 상태: 진행중, 조율중, 매칭완료, 취소
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchingStatus status;

    // 취소 사유 (매칭 요청이 취소된 경우)
    private String cancellationReason;

    // 요청 생성 시간
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = MatchingStatus.PROGRESS;
        }
    }

    public void updateStatus(MatchingStatus newStatus, String cancellationReason) {
        this.status = newStatus;
        this.cancellationReason = cancellationReason;
    }

    public void updateNegotiationDetails(Set<Day> newVisitDays, LocalTime newStartTime, LocalTime newEndTime) {
        this.visitDays = newVisitDays;
        this.startTime = newStartTime;
        this.endTime = newEndTime;
    }
}
