package com.example.amortizationschedule.service;

import com.example.amortizationschedule.model.LoanDetails;
import com.example.amortizationschedule.repository.LoanDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanDetailsServiceImpl implements LoanDetailsService {

  private final LoanDetailsRepository loanDetailsRepository;

  @Override
  public void saveLoanDetails(LoanDetails loanDetails) {
    loanDetailsRepository.save(loanDetails);
  }
}

