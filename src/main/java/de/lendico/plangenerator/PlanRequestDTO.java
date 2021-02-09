package de.lendico.plangenerator;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;

@Data
public class PlanRequestDTO {
    @Positive(message = "'loanAmount' should be a positive number")
    @NotNull(message = "'loanAmount' is required")
    private Double loanAmount;
    @Positive(message = "'nominalRate' should be a positive number")
    @NotNull(message = "'nominalRate' is required")
    private Double nominalRate;
    @Positive(message = "'duration' should be a positive number")
    @NotNull(message = "'duration' is required")
    private Integer duration;
    @NotBlank(message = "'startDate' is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}", message = "'startDate' invalid value")
    private String startDate;
}
