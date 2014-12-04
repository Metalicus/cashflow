package ru.metal.cashflow.server.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Additional parameters for report, uniq for each report
 */
public class ReportRequest {

    private Map<String, String> params = new HashMap<>();

    /**
     * Add new parameter
     *
     * @param name  name of the parameter
     * @param value value of the parameter
     */
    public void addParameter(String name, String value) {
        params.put(name, value);
    }

    /**
     * @return check if there is no additional parameters
     */
    public boolean isEmpty() {
        return params.isEmpty();
    }

    /**
     * Get int parameter
     *
     * @param name by name
     * @return value
     * @throws NumberFormatException if the string cannot be parsed
     *                               as an integer.
     */
    public int getInt(String name) {
        return Integer.valueOf(params.get(name));
    }

    /**
     * Get String parameter. Can be {@code null}
     *
     * @param name by name
     * @return value
     */
    public String getString(String name) {
        return params.get(name);
    }
}
