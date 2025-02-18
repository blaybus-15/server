package com.blaybus.server.repository;

import com.blaybus.server.domain.auth.CareGiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareGiverRepository extends JpaRepository<CareGiver, Long> {
}
