package com.example.amortizationschedule.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import lombok.Data;

@Data
@Entity
public class LoanDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty(access = Access.READ_ONLY)
  private Long loanId;
  private BigDecimal loanAmount;
  private BigDecimal depositAmount;
  private BigDecimal interestRate;
  private BigDecimal balloonPayment;
  private int monthlyPayments;
}