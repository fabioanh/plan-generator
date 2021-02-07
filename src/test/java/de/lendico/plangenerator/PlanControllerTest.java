package de.lendico.plangenerator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

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
        String expectedJson = "{\"borrowerPayments\":[{\"borrowerPaymentAmount\":1680.57,\"date\":\"2020-01-01\",\"initialOutstandingPrincipal\":5000,\"interest\":20.83,\"principal\":1659.74,\"remainingOutstandingPrincipal\":3340.26},{\"borrowerPaymentAmount\":1680.57,\"date\":\"2020-02-01\",\"initialOutstandingPrincipal\":3340.26,\"interest\":13.92,\"principal\":1666.65,\"remainingOutstandingPrincipal\":1659.69},{\"borrowerPaymentAmount\":1680.57,\"date\":\"2020-03-01\",\"initialOutstandingPrincipal\":1659.69,\"interest\":6.91,\"principal\":1673.66,\"remainingOutstandingPrincipal\":0}]}";
        String postBody = new JSONObject()
                .put("loanAmount", 5000)
                .put("nominalRate", 5.0)
                .put("duration", 3)
                .put("startDate", "2020-01-01")
                .toString();
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
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
                        fieldWithPath("error").type("String").description("Missing required Double parameter 'loanAmount'")
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
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
                        fieldWithPath("error").type("String").description("Missing required Float parameter 'nominalRate'")
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
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
                        fieldWithPath("error").type("String").description("Missing required Integer parameter 'duration'")
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
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
                        fieldWithPath("error").type("String").description("Missing required String parameter 'startDate'")
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
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
                        fieldWithPath("error").type("String").description("'loanAmount' must be greater than 0.0")
                )
        );

        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
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
                        fieldWithPath("error").type("String").description("'nominalRate' must be greater than 0.0")
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
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
                        fieldWithPath("error").type("String").description("'duration' must be greater than 0")
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
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
                .put("duration", -3)
                .put("startDate", "2020-14-40")
                .toString();

        RestDocumentationResultHandler docs = document("Error Response",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                        fieldWithPath("error").type("String").description("Invalid 'startDate' provided")
                )
        );
        // when
        ResultActions response = this.mockMvc.perform(post("/plans/generate")
                .content(postBody)
                .characterEncoding("utf-8"));
        // then

        response.andExpect(status().isBadRequest())
                .andDo(print())
                .andDo(docs);
    }
}