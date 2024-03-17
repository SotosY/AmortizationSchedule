package com.example.amortizationschedule.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.example.amortizationschedule.model.AmortizationSchedule;
import com.example.amortizationschedule.model.LoanDetails;
import com.example.amortizationschedule.repository.AmortizationEntryRepository;
import com.example.amortizationschedule.repository.AmortizationScheduleRepository;
import com.example.amortizationschedule.repository.LoanDetailsRepository;
import com.example.amortizationschedule.service.AmortizationService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AmortizationControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

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
  void testCreate() {
    // Given
    var loanDetails = new LoanDetails();
    loanDetails.setLoanAmount(BigDecimal.valueOf(20000));
    loanDetails.setDepositAmount(BigDecimal.ZERO);
    loanDetails.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails.setMonthlyPayments(12);

    // When
    ResponseEntity<AmortizationSchedule> response = restTemplate.exchange(
        "/api/amortization-schedule/create",
        org.springframework.http.HttpMethod.POST,
        new HttpEntity<>(loanDetails),
        AmortizationSchedule.class
    );

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(loanDetails, response.getBody().getLoanDetails());
    assertNotNull(response.getBody().getAmortizationEntries());
  }

  @Test
  void testFindById() {
    // Given
    var loanDetails = new LoanDetails();
    loanDetails.setLoanAmount(BigDecimal.valueOf(20000));
    loanDetails.setDepositAmount(BigDecimal.ZERO);
    loanDetails.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails.setBalloonPayment(BigDecimal.valueOf(10000));
    loanDetails.setMonthlyPayments(6);

    AmortizationSchedule amortizationSchedule = amortizationService
        .calculateAmortizationSchedule(loanDetails);
    amortizationService.createAmortizationSchedule(amortizationSchedule);

    // When
    ResponseEntity<AmortizationSchedule> response = restTemplate.getForEntity(
        "/api/amortization-schedule/" + amortizationSchedule.getId(),
        AmortizationSchedule.class
    );

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertNotNull(response.getBody().getLoanDetails());
    assertEquals(amortizationSchedule.getId(), response.getBody().getId());
    assertEquals(amortizationSchedule.getAmortizationEntries().size(),
        response.getBody().getAmortizationEntries().size());
  }

  @Test
  void testFindByIdWhenInvalid() {
    // Given

    // When
    ResponseEntity<AmortizationSchedule> response = restTemplate.getForEntity(
        "/api/amortization-schedule/" + 100L,
        AmortizationSchedule.class
    );

    // Then
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  void testGetAll() {
    // Given
    LoanDetails loanDetails1 = new LoanDetails();
    loanDetails1.setLoanAmount(BigDecimal.valueOf(20000));
    loanDetails1.setDepositAmount(BigDecimal.ZERO);
    loanDetails1.setInterestRate(BigDecimal.valueOf(7.5));
    loanDetails1.setMonthlyPayments(12);
    AmortizationSchedule amortizationSchedule1 = amortizationService
        .calculateAmortizationSchedule(loanDetails1);
    amortizationService.createAmortizationSchedule(amortizationSchedule1);

    LoanDetails loanDetails2 = new LoanDetails();
    loanDetails2.setLoanAmount(BigDecimal.valueOf(30000));
    loanDetails2.setDepositAmount(BigDecimal.ZERO);
    loanDetails2.setInterestRate(BigDecimal.valueOf(6.5));
    loanDetails2.setMonthlyPayments(24);
    AmortizationSchedule amortizationSchedule2 = amortizationService
        .calculateAmortizationSchedule(loanDetails2);
    amortizationService.createAmortizationSchedule(amortizationSchedule2);

    // When
    ResponseEntity<List<AmortizationSchedule>> response = restTemplate.exchange(
        "/api/amortization-schedule/all",
        HttpMethod.GET,
        null,
        new ParameterizedTypeReference<>() {
        });

    // Then
    assertEquals(HttpStatus.OK, response.getStatusCode());
    List<AmortizationSchedule> schedules = response.getBody();
    assertNotNull(schedules);
    assertEquals(2, schedules.size());
  }
}

