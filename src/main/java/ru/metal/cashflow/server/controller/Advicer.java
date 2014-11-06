package ru.metal.cashflow.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.metal.cashflow.server.exception.JSONException;

import java.io.PrintWriter;
import java.io.StringWriter;

@ControllerAdvice
public class Advicer {

    /**
     * Catch all of the errors and sand back to user in JSON
     *
     * @param exception ошибка
     * @return JSON представление ошибки
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public JSONException errorHandler(Exception exception) {
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
