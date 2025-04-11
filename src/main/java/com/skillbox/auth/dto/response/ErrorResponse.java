package com.skillbox.auth.dto.response;



public class ErrorResponse {
    private int error;
    private String error_description;


    public ErrorResponse(int status, String localizedMessage) {
        this.error_description = localizedMessage;
        this.error = status;
    }

    public int getError() {
        return error;
    }

    public String getError_description() {
        return error_description;
    }

}
