package org.prodapt.raf.exception;

public class CreationFailException extends RuntimeException{
    public CreationFailException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public CreationFailException() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    String errorMessage;

}
