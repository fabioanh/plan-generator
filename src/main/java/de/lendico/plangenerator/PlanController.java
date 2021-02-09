package de.lendico.plangenerator;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/plans")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @ResponseBody
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public BorrowerPaymentResponse generate(@Valid @RequestBody PlanRequestDTO planRequest) {
        return new BorrowerPaymentResponse(
                planService.generatePaymentPlan(BigDecimal.valueOf(planRequest.getLoanAmount()),
                        BigDecimal.valueOf(planRequest.getNominalRate()),
                        planRequest.getDuration(),
                        LocalDate.parse(planRequest.getStartDate())
                )
        );
    }
}
