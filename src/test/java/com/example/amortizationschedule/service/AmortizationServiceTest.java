package com.example.amortizationschedule.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.amortizationschedule.model.AmortizationEntry;
import com.example.amortizationschedule.model.AmortizationSchedule;
import com.example.amortizationschedule.model.AmortizationScheduleDetails;
import com.example.amortizationschedule.model.LoanDetails;
import com.example.amortizationschedule.repository.AmortizationEntryRepository;
import com.example.amortizationschedule.repository.AmortizationScheduleRepository;
import com.example.amortizationschedule.repository.LoanDetailsRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AmortizationServiceTest {

  @Autowired
  private AmortizationService amortizationService;

  @Autowired
  private AmortizationScheduleRepository amortizationScheduleRepository;

  @Autowired
  private AmortizationEntryRepository amortizationEntryRepository;

  @Autowired
  private LoanDetailsRepository loanDetailsRepository;

  @AfterEach
  public void tearDown() {
    amortizationScheduleRepository.deleteAll();
    loanDetailsRepository.deleteAll();
    amortizationEntryRepository.deleteAll();
  }

  @Test
  void testAmortizationScheduleWithoutBalloonPayment() {
    // Given
    var loanDetails = new LoanDetails();
    loanDetails.setLoanAmount(BigDecimal.valueOf(20000));
    loanDetails.setDepositAmount(BigDecimal.ZERO);
    loanDetails.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails.setMonthlyPayments(12);

    // When
    AmortizationSchedule actualAmortizationSchedule = amortizationService
        .calculateAmortizationSchedule(loanDetails);

    List<AmortizationEntry> expectedAmortizationSchedule = List.of(
        new AmortizationEntry(1L, 1, new BigDecimal("1735.15"), new BigDecimal("125.00"),
            new BigDecimal("1610.15"), new BigDecimal("18389.85")),
        new AmortizationEntry(2L, 2, new BigDecimal("1735.15"), new BigDecimal("114.94"),
            new BigDecimal("1620.21"), new BigDecimal("16769.64")),
        new AmortizationEntry(3L, 3, new BigDecimal("1735.15"), new BigDecimal("104.81"),
            new BigDecimal("1630.34"), new BigDecimal("15139.30")),
        new AmortizationEntry(4L, 4, new BigDecimal("1735.15"), new BigDecimal("94.62"),
            new BigDecimal("1640.53"), new BigDecimal("13498.77")),
        new AmortizationEntry(5L, 5, new BigDecimal("1735.15"), new BigDecimal("84.37"),
            new BigDecimal("1650.78"), new BigDecimal("11847.99")),
        new AmortizationEntry(6L, 6, new BigDecimal("1735.15"), new BigDecimal("74.05"),
            new BigDecimal("1661.10"), new BigDecimal("10186.89")),
        new AmortizationEntry(7L, 7, new BigDecimal("1735.15"), new BigDecimal("63.67"),
            new BigDecimal("1671.48"), new BigDecimal("8515.41")),
        new AmortizationEntry(8L, 8, new BigDecimal("1735.15"), new BigDecimal("53.22"),
            new BigDecimal("1681.93"), new BigDecimal("6833.49")),
        new AmortizationEntry(9L, 9, new BigDecimal("1735.15"), new BigDecimal("42.71"),
            new BigDecimal("1692.44"), new BigDecimal("5141.05")),
        new AmortizationEntry(10L, 10, new BigDecimal("1735.15"), new BigDecimal("32.13"),
            new BigDecimal("1703.02"), new BigDecimal("3438.03")),
        new AmortizationEntry(11L, 11, new BigDecimal("1735.15"), new BigDecimal("21.49"),
            new BigDecimal("1713.66"), new BigDecimal("1724.37")),
        new AmortizationEntry(12L, 12, new BigDecimal("1735.15"), new BigDecimal("10.78"),
            new BigDecimal("1724.37"), new BigDecimal("0.00"))
    );

    // Then
    assertEquals(12, actualAmortizationSchedule.getAmortizationEntries().size());

    for (int i = 0; i < 12; i++) {
      AmortizationEntry entry = actualAmortizationSchedule.getAmortizationEntries().get(i);
      assertEquals(expectedAmortizationSchedule.get(i).getMonthlyPayment(),
          entry.getMonthlyPayment());
      assertEquals(expectedAmortizationSchedule.get(i).getPrincipalPayment(),
          entry.getPrincipalPayment());
      assertEquals(expectedAmortizationSchedule.get(i).getInterestPayment(),
          entry.getInterestPayment());
      assertEquals(expectedAmortizationSchedule.get(i).getRemainingBalance(),
          entry.getRemainingBalance());
    }
  }

  @Test
  void testAmortizationScheduleWithBalloonPayment() {
    // Given
    var loanDetails = new LoanDetails();
    loanDetails.setLoanAmount(BigDecimal.valueOf(20000));
    loanDetails.setDepositAmount(BigDecimal.ZERO);
    loanDetails.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails.setBalloonPayment(BigDecimal.valueOf(10000));
    loanDetails.setMonthlyPayments(12);

    // When
    AmortizationSchedule actualAmortizationSchedule = amortizationService
        .calculateAmortizationSchedule(loanDetails);

    List<AmortizationEntry> expectedAmortizationSchedule = List.of(
        new AmortizationEntry(1L, 1, new BigDecimal("930.07"), new BigDecimal("125.00"),
            new BigDecimal("805.07"), new BigDecimal("19194.93")),
        new AmortizationEntry(2L, 2, new BigDecimal("930.07"), new BigDecimal("119.97"),
            new BigDecimal("810.11"), new BigDecimal("18384.82")),
        new AmortizationEntry(3L, 3, new BigDecimal("930.07"), new BigDecimal("114.91"),
            new BigDecimal("815.17"), new BigDecimal("17569.65")),
        new AmortizationEntry(4L, 4, new BigDecimal("930.07"), new BigDecimal("109.81"),
            new BigDecimal("820.26"), new BigDecimal("16749.39")),
        new AmortizationEntry(5L, 5, new BigDecimal("930.07"), new BigDecimal("104.68"),
            new BigDecimal("825.39"), new BigDecimal("15924.00")),
        new AmortizationEntry(6L, 6, new BigDecimal("930.07"), new BigDecimal("99.52"),
            new BigDecimal("830.55"), new BigDecimal("15093.45")),
        new AmortizationEntry(7L, 7, new BigDecimal("930.07"), new BigDecimal("94.33"),
            new BigDecimal("835.74"), new BigDecimal("14257.71")),
        new AmortizationEntry(8L, 8, new BigDecimal("930.07"), new BigDecimal("89.11"),
            new BigDecimal("840.96"), new BigDecimal("13416.74")),
        new AmortizationEntry(9L, 9, new BigDecimal("930.07"), new BigDecimal("83.85"),
            new BigDecimal("846.22"), new BigDecimal("12570.52")),
        new AmortizationEntry(10L, 10, new BigDecimal("930.07"), new BigDecimal("78.57"),
            new BigDecimal("851.51"), new BigDecimal("11719.02")),
        new AmortizationEntry(11L, 11, new BigDecimal("930.07"), new BigDecimal("73.24"),
            new BigDecimal("856.83"), new BigDecimal("10862.19")),
        new AmortizationEntry(12L, 12, new BigDecimal("930.07"), new BigDecimal("67.89"),
            new BigDecimal("862.19"), new BigDecimal("10000.00"))
    );

    // When
    assertEquals(12, actualAmortizationSchedule.getAmortizationEntries().size());

    for (int i = 0; i < 12; i++) {
      AmortizationEntry entry = actualAmortizationSchedule.getAmortizationEntries().get(i);
      assertEquals(expectedAmortizationSchedule.get(i).getMonthlyPayment(),
          entry.getMonthlyPayment());
      assertEquals(expectedAmortizationSchedule.get(i).getPrincipalPayment(),
          entry.getPrincipalPayment());
      assertEquals(expectedAmortizationSchedule.get(i).getInterestPayment(),
          entry.getInterestPayment());
      assertEquals(expectedAmortizationSchedule.get(i).getRemainingBalance(),
          entry.getRemainingBalance());
    }
  }

  @Test
  void testListAllAmortizationSchedulesDetails() {
    // Given
    LoanDetails loanDetails1 = new LoanDetails();
    loanDetails1.setLoanAmount(BigDecimal.valueOf(20000));
    loanDetails1.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails1.setMonthlyPayments(3);
    loanDetails1.setDepositAmount(BigDecimal.ZERO);
    loanDetails1.setBalloonPayment(BigDecimal.ZERO);

    LoanDetails loanDetails2 = new LoanDetails();
    loanDetails2.setLoanAmount(BigDecimal.valueOf(5000.00));
    loanDetails2.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails2.setMonthlyPayments(3);
    loanDetails2.setDepositAmount(BigDecimal.ZERO);
    loanDetails2.setBalloonPayment(BigDecimal.ZERO);

    AmortizationSchedule amortizationSchedule1 = amortizationService
        .calculateAmortizationSchedule(loanDetails1);
    amortizationService.createAmortizationSchedule(amortizationSchedule1);

    AmortizationSchedule amortizationSchedule2 = amortizationService
        .calculateAmortizationSchedule(loanDetails2);
    amortizationService.createAmortizationSchedule(amortizationSchedule2);

    // When
    List<AmortizationScheduleDetails> actualScheduleDetailsList = amortizationService
        .listAllAmortizationSchedulesDetails();

    // Then
    assertEquals(2, actualScheduleDetailsList.size());

    // Loan Details 1
    assertEquals(amortizationSchedule1.getLoanDetails().getLoanId(),
        actualScheduleDetailsList.get(0).getLoanDetails().getLoanId());
    assertEquals(amortizationSchedule1.getLoanDetails().getLoanAmount().longValue(),
        actualScheduleDetailsList.get(0).getLoanDetails().getLoanAmount().longValue());
    assertEquals(amortizationSchedule1.getLoanDetails().getDepositAmount().longValue(),
        actualScheduleDetailsList.get(0).getLoanDetails().getDepositAmount().longValue());
    assertEquals(amortizationSchedule1.getLoanDetails().getBalloonPayment().longValue(),
        actualScheduleDetailsList.get(0).getLoanDetails().getBalloonPayment().longValue());
    assertEquals(amortizationSchedule1.getLoanDetails().getInterestRate().longValue(),
        actualScheduleDetailsList.get(0).getLoanDetails().getInterestRate().longValue());
    assertEquals(amortizationSchedule1.getLoanDetails().getMonthlyPayments(),
        actualScheduleDetailsList.get(0).getLoanDetails().getMonthlyPayments());

    // Loan Details 2
    assertEquals(amortizationSchedule2.getLoanDetails().getLoanId(),
        actualScheduleDetailsList.get(1).getLoanDetails().getLoanId());
    assertEquals(amortizationSchedule2.getLoanDetails().getLoanAmount().longValue(),
        actualScheduleDetailsList.get(1).getLoanDetails().getLoanAmount().longValue());
    assertEquals(amortizationSchedule2.getLoanDetails().getDepositAmount().longValue(),
        actualScheduleDetailsList.get(1).getLoanDetails().getDepositAmount().longValue());
    assertEquals(amortizationSchedule2.getLoanDetails().getBalloonPayment().longValue(),
        actualScheduleDetailsList.get(1).getLoanDetails().getBalloonPayment().longValue());
    assertEquals(amortizationSchedule2.getLoanDetails().getInterestRate().longValue(),
        actualScheduleDetailsList.get(1).getLoanDetails().getInterestRate().longValue());
    assertEquals(amortizationSchedule2.getLoanDetails().getMonthlyPayments(),
        actualScheduleDetailsList.get(1).getLoanDetails().getMonthlyPayments());

    // Monthly Repayment
    assertEquals(amortizationSchedule1.getAmortizationEntries().get(0).getMonthlyPayment(),
        actualScheduleDetailsList.get(0).getMonthlyRepayment());
    assertEquals(amortizationSchedule2.getAmortizationEntries().get(0).getMonthlyPayment(),
        actualScheduleDetailsList.get(1).getMonthlyRepayment());

    // Total Interest Due
    assertEquals(BigDecimal.valueOf(250.52),
        actualScheduleDetailsList.get(0).getTotalInterestDue());
    assertEquals(BigDecimal.valueOf(62.63), actualScheduleDetailsList.get(1).getTotalInterestDue());

    // Total Payment Due
    assertEquals(BigDecimal.valueOf(20250.51),
        actualScheduleDetailsList.get(0).getTotalPaymentsDue());
    assertEquals(BigDecimal.valueOf(5062.62),
        actualScheduleDetailsList.get(1).getTotalPaymentsDue());
  }

}
