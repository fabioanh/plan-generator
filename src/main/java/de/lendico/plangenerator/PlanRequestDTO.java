package de.lendico.plangenerator;

import lombok.Data;

import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class PlanRequestDTO {
    @Positive
    private BigDecimal loanAmount;
    @Positive
    private BigDecimal nominalRate;
    @Positive
    private Integer duration;

    private String startDate;
}
