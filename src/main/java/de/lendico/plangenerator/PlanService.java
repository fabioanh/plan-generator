package de.lendico.plangenerator;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Plan service business logic
 */
@Service
public class PlanService {
    // Rounding mode to be used in all operations
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_DOWN;
    public static final int YEARLY_PERIODS = 12;
    private static final Integer SHORT_SCALE = 2;
    // Number of periods - 12 as the plan is expected to be used with its monthly values
    private static final BigDecimal NUM_PERIODS = BigDecimal.valueOf(YEARLY_PERIODS).setScale(2, ROUNDING_MODE);


    /**
     * Creates a payment plan with monthly values. Relays on the fact that <b>a year has 360 days and each month has 30 days<b/>
     *
     * @param loanAmount  total loan amount
     * @param nominalRate Annual Interest Rate
     * @param duration    Duration of the loan
     * @param startDate   Date when the first payment is expected
     * @return List of entries with monthly values for the payment plan
     */
    public List<BorrowerPayment> generatePaymentPlan(BigDecimal loanAmount, BigDecimal nominalRate,
                                                     Integer duration, LocalDate startDate) {
        List<BorrowerPayment> result = new ArrayList<>();
        // Simplified rate counting on the fact that a year has 360 days and each month has 30 days
        final BigDecimal monthlyRate = nominalRate.divide(NUM_PERIODS, 5, ROUNDING_MODE).divide(BigDecimal.valueOf(100));
        final BigDecimal annuity = calculateAnnuity(loanAmount, monthlyRate, duration);
        LocalDate periodDate = startDate;
        BigDecimal remainingOutstandingPrincipal = loanAmount;
        do {
            BigDecimal initialOutstandingPrincipal = remainingOutstandingPrincipal;
            BigDecimal interest = initialOutstandingPrincipal.multiply(monthlyRate).setScale(SHORT_SCALE, ROUNDING_MODE);
            BigDecimal principal = annuity.subtract(interest);
            remainingOutstandingPrincipal = initialOutstandingPrincipal.add(interest).subtract(annuity);

            // Last payment entry special case
            if (principal.compareTo(initialOutstandingPrincipal) > 0 ||
                    remainingOutstandingPrincipal.setScale(0, ROUNDING_MODE).compareTo(BigDecimal.ZERO) <= 0) {
                // Consideration in cases where due to rounding there is a mismatch by a couple cents
                BigDecimal lastPayment = initialOutstandingPrincipal.add(interest).compareTo(annuity) > 0 ? annuity : initialOutstandingPrincipal.add(interest);
                initialOutstandingPrincipal = lastPayment.subtract(interest);
                principal = initialOutstandingPrincipal;
                remainingOutstandingPrincipal = BigDecimal.ZERO;
                result.add(new BorrowerPayment(lastPayment, periodDate, initialOutstandingPrincipal, interest, principal, remainingOutstandingPrincipal));
                break;
            }

            result.add(new BorrowerPayment(annuity, periodDate, initialOutstandingPrincipal, interest, principal, remainingOutstandingPrincipal));
            periodDate = periodDate.plusMonths(1);
        } while (result.size() < duration);

        return result;
    }

    private BigDecimal calculateAnnuity(BigDecimal loanAmount, BigDecimal monthlyRate, Integer duration) {

        BigDecimal denominator = BigDecimal.ONE.subtract(BigDecimal.ONE.divide(BigDecimal.ONE.add(monthlyRate).pow(duration), 10, ROUNDING_MODE));
        return loanAmount.multiply(monthlyRate).divide(denominator, SHORT_SCALE, ROUNDING_MODE);
    }
}
