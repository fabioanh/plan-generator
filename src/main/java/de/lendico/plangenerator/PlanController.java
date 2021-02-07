package de.lendico.plangenerator;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/plans")
public class PlanController {

    @ResponseBody
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    public BorrowerPaymentResponse generate(){
        return new BorrowerPaymentResponse();
    }
}
