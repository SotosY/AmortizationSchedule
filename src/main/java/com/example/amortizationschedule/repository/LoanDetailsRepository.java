package com.example.amortizationschedule.repository;

import com.example.amortizationschedule.model.LoanDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoanDetailsRepository extends JpaRepository<LoanDetails, Long> {

}


