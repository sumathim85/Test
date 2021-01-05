package org.prodapt.raf.exception;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class RAFExceptionAdvice {

    @ExceptionHandler(value = org.prodapt.raf.exception.OrderDetailsNotfoundException.class)
    public ResponseEntity<?> exception(org.prodapt.raf.exception.OrderDetailsNotfoundException exception) {

        return (ResponseEntity<?>) ResponseEntity.status(404).body(exception.getErrorMessage()+" is missing.");
    }

    @ExceptionHandler(value = org.prodapt.raf.exception.DatabaseException.class)
    public ResponseEntity<?> exception(org.prodapt.raf.exception.DatabaseException exception) {
        if (exception.getErrorMessage().toString()=="Error")
            return (ResponseEntity<?>) ResponseEntity.status(413).body("Error in database connection");

        if (exception.getErrorMessage().toString()=="NO")
            return (ResponseEntity<?>) ResponseEntity.status(414).body("Order is not placed.");
        if (exception.getErrorMessage().toString()=="NO_ID")
            return (ResponseEntity<?>) ResponseEntity.status(414).body("Bot Id is not present");
        return (ResponseEntity<?>) ResponseEntity.status(411).body("Could not insert data into "+exception.getErrorMessage());
    }

    @ExceptionHandler(value = org.prodapt.raf.exception.CreationFailException.class)
    public ResponseEntity<?> exception(org.prodapt.raf.exception.CreationFailException exception) {

        return (ResponseEntity<?>) ResponseEntity.status(412).body("Could not create "+exception.getErrorMessage());
    }
}
