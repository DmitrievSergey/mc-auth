package com.skillbox.auth.dto.response;

import java.util.Date;

public class ErrorResponse {
    private int status;
    private String errorMessage;
    private Date timestamp;

    public ErrorResponse(int status, String localizedMessage) {
        this.errorMessage = localizedMessage;
        this.status = status;
        this.timestamp = new Date();
    }

    public int getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
