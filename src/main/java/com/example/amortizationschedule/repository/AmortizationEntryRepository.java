package com.example.amortizationschedule.repository;

import com.example.amortizationschedule.model.AmortizationEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmortizationEntryRepository extends JpaRepository<AmortizationEntry, Long> {

}