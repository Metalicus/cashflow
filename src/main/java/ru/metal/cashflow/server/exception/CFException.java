package ru.metal.cashflow.server.exception;

/**
 * CashFlow Exception. Checked exception for all exeptions
 */
public class CFException extends Exception {

    public CFException() {
    }

    public CFException(Throwable cause) {
        super(cause);
    }

    public CFException(String message) {
        super(message);
    }

    public CFException(String message, Throwable cause) {
        super(message, cause);
    }
}
