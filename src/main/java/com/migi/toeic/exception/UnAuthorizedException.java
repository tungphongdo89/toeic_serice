package com.migi.toeic.exception;

public class UnAuthorizedException extends RuntimeException {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String errorMessage;

    public UnAuthorizedException(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

}
