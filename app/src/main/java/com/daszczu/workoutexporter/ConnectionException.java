package com.daszczu.workoutexporter;

/**
 * Created by Daszczu on 2017-07-14.
 */

public class ConnectionException extends Exception {
    private int statusCode;
    private String message;

    public ConnectionException(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
