package de.lendico.plangenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanService planService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    public void generate_regularRequest_successfulResponse() throws Exception {
        // having
        Mockito.when(planService.generatePaymentPlan(Mockito.any(BigDecimal.class), Mockito.any(BigDecimal.class), Mockito.eq(3), Mockito.eq(LocalDate.of(2020, 1, 1))))
                .thenReturn(
                        Arrays.asList(
                                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 1, 1), BigDecimal.valueOf(5000), BigDecimal.valueOf(20.83), BigDecimal.valueOf(1659.74), BigDecimal.valueOf(3340.26)),
                                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 2, 1), BigDecimal.valueOf(3340.26), BigDecimal.valueOf(13.92), BigDecimal.valueOf(1666.65), BigDecimal.valueOf(1673.61)),
                                new BorrowerPayment(BigDecimal.valueOf(1680.57), LocalDate.of(2020, 3, 1), BigDecimal.valueOf(1673.6), BigDecimal.valueOf(6.97), BigDecimal.valueOf(1673.6), BigDecimal.ZERO)
                        )
                );
        String expectedJson = "{\"borrowerPayments\":[" +
                "{\"borrowerPaymentAmount\":1680.57,\"date\":\"2020-01-01\",\"initialOutstandingPrincipal\":5000.0,\"interest\":20.83,\"principal\":1659.74,\"remainingOutstandingPrincipal\":3340.26}," +
                "{\"borrowerPaymentAmount\":1680.57,\"date\":\"2020-02-01\",\"initialOutstandingPrincipal\":3340.26,\"interest\":13.92,\"principal\":1666.65,\"remainingOutstandingPrincipal\":1673.61}," +
                "{\"borrowerPaymentAmount\":1680.57,\"date\":\"2020-03-01\",\"initialOutstandingPrincipal\":1673.6,\"interest\":6.97,\"principal\":1673.6,\"remainingOutstandingPrincipal\":0}" +
                "]}";
        String postBody = new JSONObject()
                .put("loanAmount", 5000)
                .put("nominalRate", 5.0)
                .put("duration", 3)
                .put("startDate", "2020-01-01")
                .toString();
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json(expectedJson))
                .andDo(document("generate-plan",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(fieldWithPath("loanAmount").description("Total loan amount of the plan"),
                                fieldWithPath("nominalRate").description("Nominal rate for the loan plan"),
                                fieldWithPath("duration").description("Duration in months for the plan"),
                                fieldWithPath("startDate").description("Start date in ISO-8601 for the loan plan")
                        )
                ));
        ;
    }

    @Test
    public void generate_missingLoanAmountParam_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("nominalRate", 5.0)
                .put("duration", 3)
                .put("startDate", "2020-01-01")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'loanAmount' is required"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }

    @Test
    public void generate_missingNominalRateParam_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("loanAmount", 5000.0)
                .put("duration", 3)
                .put("startDate", "2020-01-01")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'nominalRate' is required"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }

    @Test
    public void generate_missingDurationParam_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("loanAmount", 5000.0)
                .put("nominalRate", 5.0)
                .put("startDate", "2020-01-01")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'duration' is required"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }

    @Test
    public void generate_missingStartDateParam_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("loanAmount", 5000.0)
                .put("nominalRate", 5.0)
                .put("duration", 3)
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'startDate' is required"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }

    @Test
    public void generate_negativeLoanAmount_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("loanAmount", 5000.0)
                .put("nominalRate", -5.0)
                .put("duration", 3)
                .put("startDate", "2020-01-01")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'loanAmount' should be a positive number"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );

        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().is4xxClientError())
                .andDo(print())
                .andDo(docs);
    }

    @Test
    public void generate_negativeNominalRate_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("loanAmount", 5000.0)
                .put("nominalRate", -5.0)
                .put("duration", 3)
                .put("startDate", "2020-01-01")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'nominalRate' should be a positive number"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }

    @Test
    public void generate_negativeDuration_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("loanAmount", 5000.0)
                .put("nominalRate", 5.0)
                .put("duration", -3)
                .put("startDate", "2020-01-01")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'duration' should be a positive number"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }

    @Test
    public void generate_invalidDate_errorResponse() throws Exception {
        // having
        String postBody = new JSONObject()
                .put("loanAmount", 5000.0)
                .put("nominalRate", 5.0)
                .put("duration", 3)
                .put("startDate", "2020-Nov-11")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("errors[].error").type("String").description("'startDate' invalid value"),
                        fieldWithPath("errors[].status").type("Integer").description(400)
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .contentType("application/json")
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }
}