package com.blaybus.server.repository;

import static com.blaybus.server.domain.journal.QCareJournal.careJournal;
import static com.blaybus.server.domain.senior.QSenior.senior;

import com.blaybus.server.domain.journal.CareJournal;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CareJournalQueryRepository {

    private final JPAQueryFactory queryFactory;

    public List<CareJournal> searchCareJournal(String elderName, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("CareJournal 검색 - 이름: {}, 시작날짜: {}, 종료날짜: {}", elderName, startDate, endDate);

        return queryFactory
                .selectFrom(careJournal)
                .join(careJournal.senior, senior).on(careJournal.senior.id.eq(senior.id))
                .where(
                        elderNameContains(elderName), // 🚀 어르신 이름 검색 (nullable)
                        createdAtBetween(startDate, endDate) // 🚀 날짜 범위 검색 (nullable)
                )
                .orderBy(careJournal.createdAt.desc()) // 🚀 최신순 정렬
                .fetch();
    }

    // 🚀 어르신 이름 검색 (nullable)
    private BooleanExpression elderNameContains(String elderName) {
        return StringUtils.hasText(elderName) ? careJournal.senior.name.contains(elderName) : null;
    }

    // 🚀 특정 날짜 범위 검색 (nullable)
    private BooleanExpression createdAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate != null && endDate != null) {
            return careJournal.createdAt.between(startDate, endDate);
        }
        return null;
    }
}
