package com.example.amortizationschedule.repository;

import com.example.amortizationschedule.model.AmortizationSchedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmortizationScheduleRepository extends JpaRepository<AmortizationSchedule, Long> {

  Optional<AmortizationSchedule> findById(Long id);
}

