package ru.metal.cashflow.server.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Request for filtering data
 */
public class FilterRequest {

    private Map<String, Filter> filters = new HashMap<>();

    /**
     * @return {@code true} if no filters
     */
    public boolean isEmpty() {
        return filters.isEmpty();
    }

    /**
     * Get filter by property name. We asume programmer knows about filters he works with
     *
     * @param name name of the property
     * @return filter or {@code null} if there is no such filter
     */
    public Filter getFilter(String name) {
        return filters.get(name);
    }

    /**
     * Add new filter to filter list
     *
     * @param filter new filter
     */
    public void addFilter(Filter filter) {
        filters.put(filter.getName(), filter);
    }

    public static class Filter {

        private String name;
        private String value;

        public Filter(String name, String value) {
            this.name = name;
            this.value = value;
        }

        /**
         * @return filtering property
         */
        public String getName() {
            return name;
        }

        /**
         * @return value
         */
        public String getValue() {
            return value;
        }
    }
}
