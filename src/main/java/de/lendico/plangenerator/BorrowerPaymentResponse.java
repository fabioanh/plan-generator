package de.lendico.plangenerator;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BorrowerPaymentResponse {
    private List<BorrowerPayment> borrowerPayments = new ArrayList<>();
}
