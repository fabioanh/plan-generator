package de.lendico.plangenerator;

import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;

@Value
public class BorrowerPayment {
    private BigDecimal borrowerPaymentAmount;
    private LocalDate date;
    private BigDecimal initialOutstandingPrincipal;
    private BigDecimal interest;
    private BigDecimal principal;
    private BigDecimal remainingOutstandingPrincipal;
}
