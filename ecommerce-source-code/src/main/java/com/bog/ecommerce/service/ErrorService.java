package com.bog.ecommerce.service;

import com.bog.ecommerce.model.response.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;

@Service
public class ErrorService {

    public ResponseEntity<ApiError> MVCError(Errors errors){
            return new ResponseEntity<>(new ApiError(errors), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ApiError> multipartError(){
        return new ResponseEntity<>(new ApiError("გთხოვთ ატვირთოთ სურათი"),HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<ApiError> customError(String message,HttpStatus httpStatus){
        return new ResponseEntity<>(new ApiError(message),httpStatus);
    }
}
