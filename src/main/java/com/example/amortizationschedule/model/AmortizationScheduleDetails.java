package com.example.amortizationschedule.model;

import java.math.BigDecimal;
import lombok.Data;

@Data
public class AmortizationScheduleDetails {

  private LoanDetails loanDetails;
  private BigDecimal monthlyRepayment;
  private BigDecimal totalInterestDue;
  private BigDecimal totalPaymentsDue;
}

