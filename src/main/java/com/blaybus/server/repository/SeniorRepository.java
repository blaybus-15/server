package com.blaybus.server.repository;

import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.senior.Senior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeniorRepository extends JpaRepository<Senior, Long> {
    boolean existsBySerialNumber(String serialNumber);
    List<Senior> findByCenter(Center center);
}
