package com.blaybus.server.domain.journal;

import com.blaybus.server.domain.senior.Senior;
import com.blaybus.server.domain.auth.CareGiver;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "care_journal")
public class CareJournal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "senior_id", nullable = false)
    private Senior senior;

    @ManyToOne
    @JoinColumn(name = "care_giver_id", nullable = false)
    private CareGiver careGiver;

    @Column(name = "care_giver_name", nullable = false)
    private String careGiverName;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "senior_birthday", nullable = false)
    private LocalDate seniorBirthday;

    @Column(name = "senior_gender", nullable = false)
    private String seniorGender;

    @Column(name = "senior_care_level", nullable = false)
    private String seniorCareLevel;

    @OneToOne(mappedBy = "careJournal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HealthJournal healthJournal;

    @OneToOne(mappedBy = "careJournal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ActivityJournal activityJournal;

    @OneToOne(mappedBy = "careJournal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HygieneJournal hygieneJournal;

    @OneToOne(mappedBy = "careJournal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private MedicationJournal medicationJournal;

    @OneToOne(mappedBy = "careJournal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private NotesJournal notesJournal;
}