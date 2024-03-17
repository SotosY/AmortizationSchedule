package com.example.amortizationschedule.service;

import com.example.amortizationschedule.model.AmortizationEntry;
import com.example.amortizationschedule.model.AmortizationSchedule;
import com.example.amortizationschedule.model.AmortizationScheduleDetails;
import com.example.amortizationschedule.model.LoanDetails;
import com.example.amortizationschedule.repository.AmortizationEntryRepository;
import com.example.amortizationschedule.repository.AmortizationScheduleRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link AmortizationService} interface.
 *
 * @author Sotiris.Yiallourides
 */
@Service
@RequiredArgsConstructor
public class AmortizationServiceImpl implements AmortizationService {

  private static final int INTEREST_ROUNDING_SCALE = 5;
  private static final int INPUT_ROUNDING_SCALE = 15;
  private static final int OUTPUT_ROUNDING_SCALE = 2;
  private static final int NUMBER_OF_MONTHS_IN_YEAR = 12;

  private final LoanDetailsService loanDetailsService;
  private final AmortizationScheduleRepository amortizationScheduleRepository;
  private final AmortizationEntryRepository amortizationEntryRepository;

  /**
   * Creates an amortization schedule.
   *
   * @param amortizationSchedule The amortization schedule to create.
   * @return The created amortization schedule.
   */
  @Override
  public AmortizationSchedule createAmortizationSchedule(
      AmortizationSchedule amortizationSchedule) {
    loanDetailsService.saveLoanDetails(amortizationSchedule.getLoanDetails());
    amortizationSchedule.getAmortizationEntries().forEach(amortizationEntryRepository::save);
    return amortizationScheduleRepository.save(amortizationSchedule);
  }

  /**
   * Calculates the amortization schedule for the provided loan details.
   *
   * @param loanDetails The details of the loan including loan amount, interest rate, etc.
   * @return The list of amortization entries representing the schedule.
   */
  @Override
  public AmortizationSchedule calculateAmortizationSchedule(LoanDetails loanDetails) {

    validateLoanDetails(loanDetails);

    BigDecimal yearlyInterestRateInDecimal = calculateYearlyInterestRate(
        loanDetails.getInterestRate());
    BigDecimal monthlyInterestRate = calculateMonthlyInterestRate(yearlyInterestRateInDecimal);
    BigDecimal remainingBalance = loanDetails.getLoanAmount()
        .subtract(loanDetails.getDepositAmount());
    BigDecimal balloonPayment = loanDetails.getBalloonPayment();
    int monthlyPayments = loanDetails.getMonthlyPayments();

    BigDecimal monthlyPayment = calculateMonthlyPayment(balloonPayment, remainingBalance,
        monthlyInterestRate, monthlyPayments);

    AmortizationSchedule amortizationSchedule = new AmortizationSchedule();
    List<AmortizationEntry> amortizationEntries = new ArrayList<>();

    for (int i = 1; i <= monthlyPayments; i++) {
      BigDecimal interestPayment = remainingBalance.multiply(monthlyInterestRate);
      BigDecimal principalPayment = calculatePrincipalPayment(monthlyPayment, interestPayment,
          remainingBalance);
      principalPayment = remainingBalance.min(principalPayment);
      remainingBalance = remainingBalance.subtract(principalPayment);
      AmortizationEntry entry = new AmortizationEntry();
      entry.setPeriod(i);
      entry.setMonthlyPayment(monthlyPayment.setScale(OUTPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
      entry.setInterestPayment(
          interestPayment.setScale(OUTPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
      entry.setPrincipalPayment(
          principalPayment.setScale(OUTPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
      entry.setRemainingBalance(
          remainingBalance.setScale(OUTPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
      amortizationEntries.add(entry);
      if (remainingBalance.compareTo(BigDecimal.ZERO) <= 0) {
        break;
      }
    }

    amortizationSchedule.setAmortizationEntries(amortizationEntries);
    amortizationSchedule.setLoanDetails(loanDetails);
    return amortizationSchedule;
  }

  /**
   * Retrieves a list of all amortization schedules with detailed information.
   *
   * @return A list of {@link AmortizationScheduleDetails} objects representing the details of each
   * amortization schedule.
   */
  @Override
  public List<AmortizationScheduleDetails> listAllAmortizationSchedulesDetails() {
    List<AmortizationSchedule> schedules = amortizationScheduleRepository.findAll();
    List<AmortizationScheduleDetails> scheduleDetailsList = new ArrayList<>();

    for (AmortizationSchedule schedule : schedules) {
      AmortizationScheduleDetails scheduleDetails = new AmortizationScheduleDetails();

      LoanDetails loanDetails = schedule.getLoanDetails();

      // Retrieve the first monthly payment (Its the same for each entry)
      BigDecimal monthlyRepayment = schedule.getAmortizationEntries().isEmpty() ?
          BigDecimal.ZERO : schedule.getAmortizationEntries().get(0).getMonthlyPayment();

      // Calculate total interest due
      BigDecimal totalInterestDue = schedule.getAmortizationEntries()
          .stream()
          .map(AmortizationEntry::getInterestPayment)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      // Calculate total payments due
      BigDecimal totalPaymentsDue = schedule.getAmortizationEntries()
          .stream()
          .map(AmortizationEntry::getMonthlyPayment)
          .reduce(BigDecimal.ZERO, BigDecimal::add);

      scheduleDetails.setLoanDetails(loanDetails);
      scheduleDetails.setMonthlyRepayment(monthlyRepayment
          .setScale(OUTPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
      scheduleDetails.setTotalInterestDue(totalInterestDue
          .setScale(OUTPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
      scheduleDetails.setTotalPaymentsDue(totalPaymentsDue
          .setScale(OUTPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));

      scheduleDetailsList.add(scheduleDetails);
    }

    return scheduleDetailsList;
  }


  /**
   * Validates the provided loan details.
   *
   * @param loanDetails The details of the loan to be validated.
   * @throws IllegalArgumentException If loan amount or interest rate is null, or monthly payments
   *                                  are non-positive.
   */
  private void validateLoanDetails(LoanDetails loanDetails) {
    if (loanDetails.getLoanAmount() == null || loanDetails.getInterestRate() == null) {
      throw new IllegalArgumentException("Loan amount and interest rate cannot be null");
    }
    if (loanDetails.getMonthlyPayments() <= 0) {
      throw new IllegalArgumentException("Monthly payments must be a positive integer");
    }
  }

  /**
   * Calculates the yearly interest rate in decimal form.
   *
   * @param yearlyInterestRate The yearly interest rate as a percentage.
   * @return The yearly interest rate in decimal form.
   */
  private BigDecimal calculateYearlyInterestRate(BigDecimal yearlyInterestRate) {
    return yearlyInterestRate
        .divide(BigDecimal.valueOf(100), INTEREST_ROUNDING_SCALE, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the monthly interest rate in decimal form.
   *
   * @param yearlyInterestRate The yearly interest rate as a percentage.
   * @return The monthly interest rate in decimal form.
   */
  private BigDecimal calculateMonthlyInterestRate(BigDecimal yearlyInterestRate) {
    return yearlyInterestRate
        .divide(BigDecimal.valueOf(NUMBER_OF_MONTHS_IN_YEAR), INTEREST_ROUNDING_SCALE,
            RoundingMode.HALF_UP);
  }

  /**
   * Calculates the principal payment for a given month.
   *
   * @param monthlyPayment   The total monthly payment.
   * @param interestPayment  The interest payment for the month.
   * @param remainingBalance The remaining balance on the loan.
   * @return The principal payment for the month.
   */
  private BigDecimal calculatePrincipalPayment(BigDecimal monthlyPayment,
      BigDecimal interestPayment,
      BigDecimal remainingBalance) {
    BigDecimal principalPayment = monthlyPayment.subtract(interestPayment);
    return remainingBalance.min(principalPayment);
  }

  /**
   * Calculates the monthly payment for the loan.
   *
   * @param balloonPayment      The balloon payment amount (if applicable).
   * @param remainingBalance    The remaining loan balance.
   * @param monthlyInterestRate The monthly interest rate.
   * @param monthlyPayments     The total number of monthly payments.
   * @return The monthly payment amount.
   */
  private BigDecimal calculateMonthlyPayment(BigDecimal balloonPayment, BigDecimal remainingBalance,
      BigDecimal monthlyInterestRate, int monthlyPayments) {
    return (balloonPayment == null || balloonPayment.compareTo(BigDecimal.ZERO) == 0)
        ? calculateMonthlyPaymentWithoutBalloon(remainingBalance, monthlyInterestRate,
        monthlyPayments)
        : calculateMonthlyPaymentWithBalloon(remainingBalance, monthlyInterestRate, monthlyPayments,
            balloonPayment);
  }

  /**
   * Calculates the monthly payment for the loan without a balloon payment.
   *
   * @param loanAmount          The total loan amount.
   * @param monthlyInterestRate The monthly interest rate.
   * @param termMonths          The term of the loan in months.
   * @return The monthly payment without a balloon payment.
   */
  private BigDecimal calculateMonthlyPaymentWithoutBalloon(BigDecimal loanAmount,
      BigDecimal monthlyInterestRate,
      int termMonths) {
    BigDecimal factor = monthlyInterestRate.add(BigDecimal.ONE).pow(termMonths);
    BigDecimal numerator = loanAmount.multiply(monthlyInterestRate).multiply(factor);
    BigDecimal denominator = factor.subtract(BigDecimal.ONE);
    return numerator.divide(denominator, INPUT_ROUNDING_SCALE, RoundingMode.HALF_UP);
  }

  /**
   * Calculates the monthly payment for the loan with a balloon payment.
   *
   * @param loanAmount          The total loan amount.
   * @param monthlyInterestRate The monthly interest rate.
   * @param termMonths          The term of the loan in months.
   * @param balloonPayment      The balloon payment amount.
   * @return The monthly payment with a balloon payment.
   */
  private BigDecimal calculateMonthlyPaymentWithBalloon(BigDecimal loanAmount,
      BigDecimal monthlyInterestRate,
      int termMonths, BigDecimal balloonPayment) {
    BigDecimal factor = monthlyInterestRate.add(BigDecimal.ONE).pow(termMonths);
    BigDecimal adjustedLoanAmount = loanAmount
        .subtract(balloonPayment.divide(factor, INPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
    BigDecimal numerator = adjustedLoanAmount.multiply(monthlyInterestRate);
    BigDecimal denominator = BigDecimal.ONE
        .subtract(BigDecimal.ONE.divide(factor, INPUT_ROUNDING_SCALE, RoundingMode.HALF_UP));
    return numerator.divide(denominator, INPUT_ROUNDING_SCALE, RoundingMode.HALF_UP);
  }
}