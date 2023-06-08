package com.baron.SocialMediaAPI.exception;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String message;
    private List<String> errors;
    private String error;

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }

    public ErrorResponse(String message, List<String> errors, String error) {
        this.message = message;
        this.errors = errors;
        this.error = error;
    }

    public ErrorResponse(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

}
