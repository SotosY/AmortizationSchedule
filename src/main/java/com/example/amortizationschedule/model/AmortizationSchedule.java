package com.example.amortizationschedule.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.List;
import lombok.Data;

@Data
@Entity
public class AmortizationSchedule {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(cascade = {CascadeType.MERGE})
  @JoinColumn(name = "loan_id")
  private LoanDetails loanDetails;

  @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  private List<AmortizationEntry> amortizationEntries;
}

