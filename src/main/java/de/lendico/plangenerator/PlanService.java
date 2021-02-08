package de.lendico.plangenerator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Plan service business logic
 */
@Service
public class PlanService {
    public List<BorrowerPayment> generatePaymentPlan(BigDecimal loanAmount, BigDecimal nominalRate,
                                                     Integer duration, LocalDate startDate) {
        return null;

    }
}
