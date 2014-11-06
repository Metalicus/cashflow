package ru.metal.cashflow.server.exception;

/**
 * Ошибка конвертируемая потом в JSON
 */
public class JSONException {

    private String message;
    private String stack;

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStack(String stack) {
        this.stack = stack;
    }

    public String getMessage() {
        return message;
    }

    public String getStack() {
        return stack;
    }
}
