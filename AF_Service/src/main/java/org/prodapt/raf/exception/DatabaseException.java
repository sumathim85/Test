package org.prodapt.raf.exception;

public class DatabaseException extends RuntimeException {

    String errorMessage;


    public DatabaseException( String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public DatabaseException() {
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
