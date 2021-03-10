package com.bog.ecommerce.model.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.Errors;

@Data
@NoArgsConstructor
public class ApiError {
    private String message;
    private HttpStatus status;

    public ApiError(Errors error) {
        this.message = null == error.getFieldError().getDefaultMessage() ? "ვალიდაცია წარუმატებლად დასრულდა" : error.getFieldError().getDefaultMessage();
        this.status = HttpStatus.BAD_REQUEST;
    }
    public ApiError(String message) {
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST;
    }
}
