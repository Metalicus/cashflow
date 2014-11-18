package imp;

import ru.metal.cashflow.server.model.*;
import ru.metal.cashflow.server.model.Currency;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;

public class ImportFromAccounting {

    public static void main(String[] args) throws Exception {
        final Properties connectionProperties = new Properties();
        connectionProperties.put("user", "sa");
        connectionProperties.put("password", "sa");

        final Connection connectionOld = DriverManager.getConnection(
                "jdbc:h2:file:C:\\data\\accounting", connectionProperties
        );
        System.out.println("Old database connected!");

        final Connection connectionNew = DriverManager.getConnection(
                "jdbc:h2:file:C:\\data\\cashflow", connectionProperties
        );
        System.out.println("New database connected!");

        System.out.println("Collect all currencies...");
        final Map<Integer, Currency> currencies = new HashMap<>();
        try (final PreparedStatement statement = connectionOld.prepareStatement("select id, name from currency")) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Currency currency = new Currency();
                currency.setId(resultSet.getInt("id"));
                currency.setName(resultSet.getString("name"));
                currencies.put(currency.getId(), currency);
            }
        }

        System.out.println("Collect all categories...");
        final Map<Integer, Category> categories = new HashMap<>();
        Integer transferId = null;
        try (final PreparedStatement statement = connectionOld.prepareStatement("select id from category where name = ?")) {
            statement.setString(1, "Перевод средств");
            final ResultSet resultSet = statement.executeQuery();
            if (resultSet.next())
                transferId = resultSet.getInt("id");
            else {
                System.out.println("Can't find transfer category");
                System.exit(-1);
            }
        }

        try (final PreparedStatement statement = connectionOld.prepareStatement("select id, name from category")) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Category category = new Category();
                category.setId(resultSet.getInt("id"));
                category.setName(resultSet.getString("name"));
                categories.put(category.getId(), category);
            }
        }

        System.out.println("Collect all accounts...");
        final Map<Integer, Account> accounts = new HashMap<>();
        try (final PreparedStatement statement = connectionOld.prepareStatement("select id, name, currency_id, balance from account")) {
            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Account account = new Account();
                account.setId(resultSet.getInt("id"));
                account.setName(resultSet.getString("name"));
                account.setCurrency(currencies.get(resultSet.getInt("currency_id")));
                account.setBalance(resultSet.getBigDecimal("balance"));
                accounts.put(account.getId(), account);
            }
        }

        System.out.println("Collect all operations...");
        final List<Operation> operations = new ArrayList<>();
        try (final PreparedStatement statement = connectionOld.prepareStatement(
                "select id, date, account_id, currency_id, category_id, info, " +
                        "exchangeRate, total, totalCurrency, moneyWas, moneyBecome, income from balance")) {

            final ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                final Operation operation = new Operation();
                operation.setId(resultSet.getInt("id"));
                operation.setDate(resultSet.getTimestamp("date"));
                operation.setAccount(accounts.get(resultSet.getInt("account_id")));
                operation.setCurrency(currencies.get(resultSet.getInt("currency_id")));
                operation.setCategory(categories.get(resultSet.getInt("category_id")));
                operation.setInfo(resultSet.getString("info"));
                operation.setAmount(resultSet.getBigDecimal("total"));
                operation.setMoneyWas(resultSet.getBigDecimal("moneyWas"));
                operation.setMoneyBecome(resultSet.getBigDecimal("moneyBecome"));

                if (resultSet.getInt("category_id") == transferId) {
                    operation.setType(Operation.FlowType.TRANSFER);
                } else {
                    operation.setType(resultSet.getBoolean("income") ? Operation.FlowType.INCOME : Operation.FlowType.EXPENSE);
                }

                final BigDecimal exchangeRate = resultSet.getBigDecimal("exchangeRate");
                if (exchangeRate != null) {
                    final CrossCurrency crossCurrency = new CrossCurrency();
                    crossCurrency.setAmount(resultSet.getBigDecimal("totalCurrency"));
                    crossCurrency.setExchangeRate(exchangeRate);
                    operation.setCrossCurrency(crossCurrency);
                }

                if (operation.getAmount().signum() < 0)
                    operation.setAmount(operation.getAmount().negate());

                operations.add(operation);
            }
        }

        System.out.println("Collected " + operations.size() + " operations");
        System.out.println("Data is collected. Let's store it in the new database");

        System.out.println("Inserting currencies...");
        try (final PreparedStatement statement = connectionNew.prepareStatement(
                "insert into currency (id, name) values (?, ?)")) {
            for (Currency currency : currencies.values()) {
                statement.setInt(1, currency.getId());
                statement.setString(2, currency.getName());
                statement.executeUpdate();
            }
        }

        System.out.println("Inserting categories...");
        try (final PreparedStatement statement = connectionNew.prepareStatement(
                "insert into category (id, name) values (?, ?)")) {
            for (Category category : categories.values()) {
                statement.setInt(1, category.getId());
                statement.setString(2, category.getName());
                statement.executeUpdate();
            }
        }

        System.out.println("Inserting accounts...");
        try (final PreparedStatement statement = connectionNew.prepareStatement(
                "insert into account (id, name, currency_id, balance) values (?, ?, ?, ?)")) {
            for (Account account : accounts.values()) {
                statement.setInt(1, account.getId());
                statement.setString(2, account.getName());
                statement.setInt(3, account.getCurrency().getId());
                statement.setBigDecimal(4, account.getBalance());
                statement.executeUpdate();
            }
        }

        System.out.println("Inserting cross currencies...");
        try (final PreparedStatement statement = connectionNew.prepareStatement(
                "insert into crosscurrency (exchangeRate, amount) values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
            for (Operation operation : operations) {
                final CrossCurrency crossCurrency = operation.getCrossCurrency();
                if (crossCurrency != null) {
                    statement.setBigDecimal(1, crossCurrency.getExchangeRate());
                    statement.setBigDecimal(2, crossCurrency.getAmount());
                    statement.executeUpdate();

                    final ResultSet generatedKeys = statement.getGeneratedKeys();
                    generatedKeys.next();
                    crossCurrency.setId(generatedKeys.getInt(1));
                }
            }
        }

        System.out.println("Inserting operations...");
        try (final PreparedStatement statement = connectionNew.prepareStatement(
                "insert into operation (id, date, account_id, currency_id, " +
                        "category_id, type, amount, moneyWas, moneyBecome, info, crosscurrency_id) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (Operation operation : operations) {
                statement.setInt(1, operation.getId());
                statement.setTimestamp(2, new Timestamp(operation.getDate().getTime()));
                statement.setInt(3, operation.getAccount().getId());
                statement.setInt(4, operation.getCurrency().getId());
                statement.setInt(5, operation.getCategory().getId());
                statement.setInt(6, operation.getType().ordinal());
                statement.setBigDecimal(7, operation.getAmount());
                statement.setBigDecimal(8, operation.getMoneyWas());
                statement.setBigDecimal(9, operation.getMoneyBecome());
                statement.setString(10, operation.getInfo());
                statement.setObject(11, operation.getCrossCurrency() == null ? null : operation.getCrossCurrency().getId());
                statement.executeUpdate();
            }
        }
    }
}
