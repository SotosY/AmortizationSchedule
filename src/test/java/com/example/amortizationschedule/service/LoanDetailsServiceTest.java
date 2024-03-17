package com.example.amortizationschedule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.amortizationschedule.model.LoanDetails;
import com.example.amortizationschedule.repository.LoanDetailsRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class LoanDetailsServiceTest {

  @Autowired
  private LoanDetailsService loanDetailsService;

  @Autowired
  private LoanDetailsRepository loanDetailsRepository;

  @AfterEach
  public void tearDown() {
    loanDetailsRepository.deleteAll();
  }

  @Test
  void testSaveLoanDetails() {
    // Given
    var loanDetails = new LoanDetails();
    loanDetails.setLoanAmount(BigDecimal.valueOf(20000));
    loanDetails.setDepositAmount(BigDecimal.ZERO);
    loanDetails.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails.setMonthlyPayments(12);

    // When
    loanDetailsService.saveLoanDetails(loanDetails);

    // Then
    assertEquals(1, loanDetailsRepository.findAll().size());
  }
}
