package org.prodapt.raf.exception;

public class OrderDetailsNotfoundException extends RuntimeException{
    public OrderDetailsNotfoundException(String test) {
        this.errorMessage =test;
    }

    public OrderDetailsNotfoundException() {
    }


    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    String errorMessage;


//    private static final long serialVersionUID = 1L;
}
