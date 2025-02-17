package com.blaybus.server.repository;

import com.blaybus.server.domain.Center;
import com.blaybus.server.domain.senior.Senior;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeniorRepository extends JpaRepository<Senior, Long> {
    boolean existsBySerialNumber(String serialNumber);
    List<Senior> findByCenter(Center center);
}
