package de.lendico.plangenerator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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
                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 2, 1), BigDecimal.valueOf(3340.26), BigDecimal.valueOf(13.92), BigDecimal.valueOf(1666.65), BigDecimal.valueOf(1659.69)),
                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 3, 1), BigDecimal.valueOf(1659.69), BigDecimal.valueOf(6.91), BigDecimal.valueOf(1673.66), BigDecimal.valueOf(0))
        );

        // when
        List<BorrowerPayment> result = planService.generatePaymentPlan(loanAmount, nominalRate, duration, startDate);

        // then
        assertEquals(duration, result.size());
        assertEquals(expectedPlan, result);
    }

    @Test
    public void generatePaymentPlan_singleEntryPaymentPlan_successfulListOfSingleValue() {
        // having
        BigDecimal loanAmount = BigDecimal.valueOf(5000);
        BigDecimal nominalRate = BigDecimal.valueOf(5.0);
        Integer duration = 1;
        LocalDate startDate = LocalDate.of(2020, 1, 1);

        List<BorrowerPayment> expectedPlan = Collections.singletonList(
                new BorrowerPayment(BigDecimal.valueOf(5020.83), LocalDate.of(2020, 1, 1), BigDecimal.valueOf(5000), BigDecimal.valueOf(20.83), BigDecimal.valueOf(5000), BigDecimal.valueOf(0))
        );

        // when
        List<BorrowerPayment> result = planService.generatePaymentPlan(loanAmount, nominalRate, duration, startDate);

        // then
        assertEquals(1, result.size());
        assertEquals(expectedPlan, result);
    }

}