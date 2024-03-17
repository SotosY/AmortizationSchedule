package com.example.amortizationschedule.service;

import com.example.amortizationschedule.model.AmortizationSchedule;
import com.example.amortizationschedule.model.AmortizationScheduleDetails;
import com.example.amortizationschedule.model.LoanDetails;
import java.util.List;

public interface AmortizationService {

  AmortizationSchedule createAmortizationSchedule(AmortizationSchedule amortizationSchedule);

  AmortizationSchedule calculateAmortizationSchedule(LoanDetails loanDetails);

  List<AmortizationScheduleDetails> listAllAmortizationSchedulesDetails();
}

