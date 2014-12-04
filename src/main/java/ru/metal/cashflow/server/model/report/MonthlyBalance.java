package ru.metal.cashflow.server.model.report;

import ru.metal.cashflow.server.model.business.Category;
import ru.metal.cashflow.server.model.business.Currency;
import ru.metal.cashflow.server.model.business.Operation;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    public Map<Category, Map<Currency, BigDecimal>> getExpense() {
        return expense;
    }

    /**
     * @return incomes grouped by category and currency
     */
    public Map<Category, Map<Currency, BigDecimal>> getIncome() {
        return income;
    }

    /**
     * @return transfer grouped by category and currency
     */
    public Map<Category, Map<Currency, BigDecimal>> getTransfer() {
        return transfer;
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

    /**
     * Prepare calculated lines before send to client. Fill with ZERO
     */
    public void prepare() {
        for (Currency currency : currencies) {
            expense.keySet().stream().filter(category -> !expense.get(category).containsKey(currency)).forEach(category -> expense.get(category).put(currency, BigDecimal.ZERO));
            income.keySet().stream().filter(category -> !income.get(category).containsKey(currency)).forEach(category -> income.get(category).put(currency, BigDecimal.ZERO));
            transfer.keySet().stream().filter(category -> !transfer.get(category).containsKey(currency)).forEach(category -> transfer.get(category).put(currency, BigDecimal.ZERO));
        }
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

    @Override
    public ReportType getReportType() {
        return ReportType.MONTHLY_BALANCE;
    }
}
