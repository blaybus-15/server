package com.blaybus.server.repository;

import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.matching.Matching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchingRepository extends JpaRepository<Matching, Long> {
    List<Matching> findByCaregiverId(Long id);
}
