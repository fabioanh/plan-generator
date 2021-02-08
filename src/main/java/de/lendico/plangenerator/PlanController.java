package de.lendico.plangenerator;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;

@RestController
@RequestMapping("/plans")
public class PlanController {

    private PlanService planService;

    @ResponseBody
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public BorrowerPaymentResponse generate(@Valid @RequestBody PlanRequestDTO planRequest){
        return new BorrowerPaymentResponse();
    }
}
