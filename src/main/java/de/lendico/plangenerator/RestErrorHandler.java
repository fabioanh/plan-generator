package de.lendico.plangenerator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestErrorHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        log.debug("Invalid Parameter Exception", exception);

        BindingResult result = exception.getBindingResult();
        List<ErrorBody> fieldErrors = result.getFieldErrors().stream()
                .map(error -> new ErrorBody(error.getDefaultMessage(), status.value()))
                .collect(Collectors.toList());

        return new ResponseEntity(new ErrorsWrapper(fieldErrors), headers, status);
    }

    @Data
    @AllArgsConstructor
    private static class ErrorsWrapper {
        private List<ErrorBody> errors;
    }

    @Data
    @AllArgsConstructor
    private static class ErrorBody {
        private String error;
        private Integer status;
    }
}
