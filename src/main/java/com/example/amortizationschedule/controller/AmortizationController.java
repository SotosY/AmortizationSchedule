package com.example.amortizationschedule.controller;

import com.example.amortizationschedule.model.AmortizationSchedule;
import com.example.amortizationschedule.model.AmortizationScheduleDetails;
import com.example.amortizationschedule.model.LoanDetails;
import com.example.amortizationschedule.repository.AmortizationScheduleRepository;
import com.example.amortizationschedule.service.AmortizationService;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/amortization-schedule")
@RequiredArgsConstructor
public class AmortizationController {

  private final AmortizationService amortizationService;
  private final AmortizationScheduleRepository amortizationScheduleRepository;

  @PostMapping("/create")
  public ResponseEntity<AmortizationSchedule> create(@RequestBody LoanDetails loanDetails) {
    var amortizationSchedule = amortizationService.calculateAmortizationSchedule(loanDetails);
    return ResponseEntity.ok(amortizationService.createAmortizationSchedule(amortizationSchedule));
  }

  @GetMapping("/all")
  public ResponseEntity<List<AmortizationScheduleDetails>> getAll() {
    return ResponseEntity.ok(amortizationService.listAllAmortizationSchedulesDetails());
  }

  @GetMapping("/{id}")
  public ResponseEntity<AmortizationSchedule> getScheduleById(@PathVariable Long id) {
    Optional<AmortizationSchedule> optionalSchedule = amortizationScheduleRepository.findById(id);
    return optionalSchedule.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}

