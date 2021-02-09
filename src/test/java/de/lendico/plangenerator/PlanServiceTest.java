package de.lendico.plangenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SpringTestConfiguration.class})
class PlanServiceTest {

    @Autowired
    private PlanService planService;

    @Test
    public void generatePaymentPlan_regularPaymentPlan_successfulListOfValues() {
        // having
        BigDecimal loanAmount = BigDecimal.valueOf(5000);
        BigDecimal nominalRate = BigDecimal.valueOf(5.0);
        Integer duration = 3;
        LocalDate startDate = LocalDate.of(2020, 1, 1);

        List<BorrowerPayment> expectedPlan = Arrays.asList(
                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 1, 1), BigDecimal.valueOf(5000), BigDecimal.valueOf(20.83), BigDecimal.valueOf(1659.74), BigDecimal.valueOf(3340.26)),
                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 2, 1), BigDecimal.valueOf(3340.26), BigDecimal.valueOf(13.92), BigDecimal.valueOf(1666.65), BigDecimal.valueOf(1673.61)),
                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 3, 1), BigDecimal.valueOf(1673.6), BigDecimal.valueOf(6.97), BigDecimal.valueOf(1673.6), BigDecimal.ZERO)
        );

        // when
        List<BorrowerPayment> result = planService.generatePaymentPlan(loanAmount, nominalRate, duration, startDate);

        // then
        assertEquals(duration, result.size());
        assertEqualBorrowPayments(expectedPlan, result);
    }

    @Test
    public void generatePaymentPlan_differentLastPeriodPaymentAmount_lastPeriodGoodValues() {
        // having
        BigDecimal loanAmount = BigDecimal.valueOf(5000);
        BigDecimal nominalRate = BigDecimal.valueOf(5.0);
        Integer duration = 24;
        LocalDate startDate = LocalDate.of(2020, 1, 1);

        List<BorrowerPayment> expectedLastEntryPlan = Collections.singletonList(
                new BorrowerPayment(BigDecimal.valueOf(219.28), LocalDate.of(2021, 12, 1), BigDecimal.valueOf(218.37), BigDecimal.valueOf(0.91), BigDecimal.valueOf(218.37), BigDecimal.ZERO)
        );

        // when
        List<BorrowerPayment> result = planService.generatePaymentPlan(loanAmount, nominalRate, duration, startDate);

        // then
        assertEquals(duration, result.size());
        assertEqualBorrowPayments(expectedLastEntryPlan, Collections.singletonList(result.get(23)));
    }

    @Test
    public void generatePaymentPlan_singleEntryPaymentPlan_successfulListOfSingleValue() {
        // having
        BigDecimal loanAmount = BigDecimal.valueOf(5000);
        BigDecimal nominalRate = BigDecimal.valueOf(5.0);
        Integer duration = 1;
        LocalDate startDate = LocalDate.of(2020, 1, 1);

        List<BorrowerPayment> expectedPlan = Collections.singletonList(
                new BorrowerPayment(BigDecimal.valueOf(5020.83), LocalDate.of(2020, 1, 1), BigDecimal.valueOf(5000).setScale(2, RoundingMode.HALF_DOWN), BigDecimal.valueOf(20.83), BigDecimal.valueOf(5000), BigDecimal.valueOf(0))
        );

        // when
        List<BorrowerPayment> result = planService.generatePaymentPlan(loanAmount, nominalRate, duration, startDate);

        // then
        assertEquals(1, result.size());
        assertEqualBorrowPayments(expectedPlan, result);
    }

    private void assertEqualBorrowPayments(List<BorrowerPayment> borrowerPayments, List<BorrowerPayment> results) {
        for (int i = 0; i < borrowerPayments.size(); i++) {
            BorrowerPayment expected = borrowerPayments.get(i);
            BorrowerPayment result = results.get(i);
            assertEquals(0, expected.getBorrowerPaymentAmount().compareTo(result.getBorrowerPaymentAmount()));
            assertEquals(0, expected.getInterest().compareTo(result.getInterest()));
            assertEquals(0, expected.getPrincipal().compareTo(result.getPrincipal()));
            assertEquals(0, expected.getInitialOutstandingPrincipal().compareTo(result.getInitialOutstandingPrincipal()));
            assertEquals(0, expected.getRemainingOutstandingPrincipal().compareTo(result.getRemainingOutstandingPrincipal()));
            assertEquals(expected.getDate(), result.getDate());
        }
    }

}