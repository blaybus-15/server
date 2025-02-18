package com.blaybus.server.service;

import com.blaybus.server.domain.auth.CareGiver;
import com.blaybus.server.domain.journal.*;
import com.blaybus.server.domain.senior.Senior;
import com.blaybus.server.dto.request.CareJournalRequest;
import com.blaybus.server.dto.request.CareJournalSearchRequest;
import com.blaybus.server.repository.CareJournalQueryRepository;
import com.blaybus.server.repository.CareJournalRepository;
import com.blaybus.server.repository.CareGiverRepository;
import com.blaybus.server.repository.SeniorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CareJournalService {

    private final CareJournalRepository careJournalRepository;
    private final CareGiverRepository careGiverRepository;
    private final SeniorRepository seniorRepository;
    private final CareJournalQueryRepository careJournalQueryRepository;

    public CareJournal createCareJournal(Long seniorId, CareJournalRequest request) {
        CareGiver careGiver = careGiverRepository.findById(request.getCareGiverId())
                .orElseThrow(() -> new RuntimeException("CareGiver not found"));
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new RuntimeException("Senior not found"));

        CareJournal careJournal = buildCareJournalFromRequest(request, careGiver, senior);
        return careJournalRepository.save(careJournal);
    }

    public List<CareJournal> getCareJournalsBySeniorId(Long seniorId) {
        return careJournalRepository.findBySeniorId(seniorId);
    }

    public Optional<CareJournal> getCareJournalBySeniorIdAndJournalId(Long seniorId, Long careJournalId) {
        return careJournalRepository.findBySeniorIdAndId(seniorId, careJournalId);
    }

    public CareJournal updateCareJournal(Long seniorId, Long careJournalId, CareJournalRequest request) {
        CareJournal careJournal = careJournalRepository.findBySeniorIdAndId(seniorId, careJournalId)
                .orElseThrow(() -> new RuntimeException("CareJournal not found"));
        CareGiver careGiver = careGiverRepository.findById(request.getCareGiverId())
                .orElseThrow(() -> new RuntimeException("CareGiver not found"));
        Senior senior = seniorRepository.findById(seniorId)
                .orElseThrow(() -> new RuntimeException("Senior not found"));

        CareJournal updatedJournal = buildCareJournalFromRequest(request, careGiver, senior);
        updatedJournal.setId(careJournalId);
        return careJournalRepository.save(updatedJournal);
    }

    public void deleteCareJournal(Long seniorId, Long careJournalId) {
        CareJournal careJournal = careJournalRepository.findBySeniorIdAndId(seniorId, careJournalId)
                .orElseThrow(() -> new RuntimeException("CareJournal not found"));
        careJournalRepository.delete(careJournal);
    }

    public List<CareJournal> searchCareJournal(CareJournalSearchRequest careJournalSearchRequest) {
        return careJournalQueryRepository.searchCareJournal(
                careJournalSearchRequest.getSeniorName(),
                careJournalSearchRequest.getStartDate(),
                careJournalSearchRequest.getEndDate()
        );
    }

    /**
     * 요청 정보와 관련된 케어기버 및 시니어 엔티티를 사용하여 케어일지를 생성합니다.
     * @param request 케어일지 생성 요청 정보
     * @param careGiver 케어기버 엔티티
     * @param senior 시니어 엔티티
     * @return 생성된 케어일지 엔티티
     */
    private CareJournal buildCareJournalFromRequest(CareJournalRequest request, CareGiver careGiver, Senior senior) {
        return CareJournal.builder()
                .senior(senior)
                .careGiver(careGiver)
                .careGiverName(careGiver.getName())
                .createdAt(LocalDateTime.now())
                .seniorBirthday(senior.getBirthDate())
                .seniorGender(senior.getGenderType().name())
                .seniorCareLevel(senior.getCareLevel().name())
                .healthJournal(buildHealthJournal(request))
                .activityJournal(buildActivityJournal(request))
                .hygieneJournal(buildHygieneJournal(request))
                .medicationJournal(buildMedicationJournal(request))
                .notesJournal(buildNotesJournal(request))
                .build();
    }

    /**
     * 요청 정보를 사용하여 HealthJournal 엔티티를 생성합니다.
     * @param request HealthJournal 생성 요청 정보
     * @return 생성된 HealthJournal 엔티티
     */
    private HealthJournal buildHealthJournal(CareJournalRequest request) {
        return HealthJournal.builder()
                .meal(request.getMeal())
                .breakfast(request.getBreakfast())
                .lunch(request.getLunch())
                .dinner(request.getDinner())
                .snack(request.getSnack())
                .restroomSmall(request.getRestroomSmall())
                .restroomSmallMethod(request.getRestroomSmallMethod())
                .restroomSmallCondition(request.getRestroomSmallCondition())
                .restroomBig(request.getRestroomBig())
                .restroomBigMethod(request.getRestroomBigMethod())
                .restroomBigCondition(request.getRestroomBigCondition())
                .build();
    }

    /**
     * 요청 정보를 사용하여 ActivityJournal 엔티티를 생성합니다.
     * @param request ActivityJournal 생성 요청 정보
     * @return 생성된 ActivityJournal 엔티티
     */
    private ActivityJournal buildActivityJournal(CareJournalRequest request) {
        return ActivityJournal.builder()
                .sleepTime(request.getSleepTime())
                .sleepQuality(request.getSleepQuality())
                .activity(request.getActivity())
                .emotion(request.getEmotion())
                .build();
    }

    /**
     * 요청 정보를 사용하여 HygieneJournal 엔티티를 생성합니다.
     * @param request HygieneJournal 생성 요청 정보
     * @return 생성된 HygieneJournal 엔티티
     */
    private HygieneJournal buildHygieneJournal(CareJournalRequest request) {
        return HygieneJournal.builder()
                .bath(request.getBath())
                .skinCondition(request.getSkinCondition())
                .build();
    }

    /**
     * 요청 정보를 사용하여 MedicationJournal 엔티티를 생성합니다.
     * @param request MedicationJournal 생성 요청 정보
     * @return 생성된 MedicationJournal 엔티티
     */
    private MedicationJournal buildMedicationJournal(CareJournalRequest request) {
        return MedicationJournal.builder()
                .medicationTime(request.getMedicationTime())
                .medicationName(request.getMedicationName())
                .build();
    }

    /**
     * 요청 정보를 사용하여 NotesJournal 엔티티를 생성합니다.
     * @param request NotesJournal 생성 요청 정보
     * @return 생성된 NotesJournal 엔티티
     */
    private NotesJournal buildNotesJournal(CareJournalRequest request) {
        return NotesJournal.builder()
                .specialNotes(request.getSpecialNotes())
                .adminNote(request.getAdminNote())
                .build();
    }
}