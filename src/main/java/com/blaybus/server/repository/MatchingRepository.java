package com.blaybus.server.repository;

import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.matching.Matching;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchingRepository extends JpaRepository<Matching, Long> {
    List<Matching> findByCaregiverId(Long id);
}
