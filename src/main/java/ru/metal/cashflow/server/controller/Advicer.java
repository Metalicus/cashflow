package ru.metal.cashflow.server.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.exception.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class Advicer {

    private static final Log logger = LogFactory.getLog(Advicer.class);

    /**
     * Catch only TypeMismatchException
     *
     * @param exception error
     * @return JSON response to user
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(TypeMismatchException.class)
    @ResponseBody
    public JSONException typeMismacthHandler(TypeMismatchException exception) {

        logger.error(exception);

        final JSONException exceptionToJSON = new JSONException();
        exceptionToJSON.setMessage("Wrong request parameter");
        exceptionToJSON.setStack("");

        return exceptionToJSON;
    }

    /**
     * Catch all of the errors and send back to user in JSON
     *
     * @param exception error
     * @return JSON response to user
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public JSONException errorHandler(Exception exception) {
        if (!(exception instanceof CFException)) {
            // uncaught exception, we should log it properly
            logger.error(exception);
        }

        final StringWriter stringWriter = new StringWriter();
        exception.printStackTrace(new PrintWriter(stringWriter));

        String message = exception.getMessage();
        if (message == null) {
            if (exception.getCause() != null)
                message = exception.getCause().getClass().getName();
            else
                message = exception.getClass().getName();
        }

        final JSONException exceptionToJSON = new JSONException();
        exceptionToJSON.setMessage(message);
        exceptionToJSON.setStack(stringWriter.toString());

        return exceptionToJSON;
    }
}
