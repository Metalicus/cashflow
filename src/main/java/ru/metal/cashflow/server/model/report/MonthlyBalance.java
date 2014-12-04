package ru.metal.cashflow.server.model.report;

import ru.metal.cashflow.server.model.business.Category;
import ru.metal.cashflow.server.model.business.Currency;
import ru.metal.cashflow.server.model.business.Operation;

import java.math.BigDecimal;
import java.util.*;

/**
 * Model of the Monthly Balance report
 */
public class MonthlyBalance extends Report {

    private Map<Category, Map<Currency, BigDecimal>> expense = new HashMap<>();
    private Map<Category, Map<Currency, BigDecimal>> income = new HashMap<>();
    private Map<Category, Map<Currency, BigDecimal>> transfer = new HashMap<>();

    private final Set<Currency> currencies = new HashSet<>();

    /**
     * @return expenses grouped by category and currency
     */
    public Type getExpense() {
        return prepareType(expense);
    }

    /**
     * @return incomes grouped by category and currency
     */
    public Type getIncome() {
        return prepareType(income);
    }

    /**
     * @return transfer grouped by category and currency
     */
    public Type getTransfer() {
        return prepareType(transfer);
    }

    /**
     * Add new row to report
     *
     * @param category operation's category
     * @param currency operation's currency
     * @param type     operation's type
     * @param sum      operation's sum
     */
    public void addRow(Category category, Currency currency, Operation.FlowType type, BigDecimal sum) {
        switch (type) {
            case EXPENSE:
                proceed(expense, category, currency, sum);
                break;
            case INCOME:
                proceed(income, category, currency, sum);
                break;
            case TRANSFER:
                proceed(transfer, category, currency, sum);
                break;
        }

        currencies.add(currency);
    }

    private void proceed(Map<Category, Map<Currency, BigDecimal>> map, Category category, Currency currency, BigDecimal sum) {
        if (!map.containsKey(category))
            map.put(category, new HashMap<>());

        final Map<Currency, BigDecimal> rows = map.get(category);
        if (!rows.containsKey(currency))
            rows.put(currency, BigDecimal.ZERO);

        final BigDecimal byCurrency = rows.get(currency);
        rows.put(currency, byCurrency.add(sum));
    }

    private Type prepareType(Map<Category, Map<Currency, BigDecimal>> map) {
        final Type type = new Type();
        currencies.stream().forEach(type::addCurrency);

        for (Category category : map.keySet()) {
            final Row row = new Row();
            row.setCategory(category);

            BigDecimal byCurrency;
            for (Currency currency : currencies) {
                byCurrency = map.get(category).get(currency);
                row.addValue(byCurrency == null ? BigDecimal.ZERO : byCurrency);
            }

            type.addRow(row);
        }

        return type;
    }

    @Override
    public ReportType getReportType() {
        return ReportType.MONTHLY_BALANCE;
    }

    public static class Type {

        private List<Currency> currencies = new ArrayList<>();
        private List<Row> rows = new ArrayList<>();
        private List<BigDecimal> totals = new ArrayList<>();

        public List<BigDecimal> getTotals() {
            return totals;
        }

        public List<Currency> getCurrencies() {
            return currencies;
        }

        public void addCurrency(Currency currency) {
            currencies.add(currency);
        }

        public List<Row> getRows() {
            return rows;
        }

        public void addRow(Row row) {
            if (totals.isEmpty()) {
                for (int i = 0; i < row.getValues().size(); i++)
                    totals.add(BigDecimal.ZERO);
            }

            BigDecimal value;
            for (int i = 0; i < row.getValues().size(); i++) {
                value = row.getValues().get(i);
                totals.set(i, totals.get(i).add(value));
            }

            rows.add(row);
        }

    }

    public static class Row {

        private Category category;
        private List<BigDecimal> values = new ArrayList<>();

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }

        public List<BigDecimal> getValues() {
            return values;
        }

        public void addValue(BigDecimal value) {
            values.add(value);
        }
    }
}
