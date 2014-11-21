package imp;

import ru.metal.cashflow.server.model.*;
import ru.metal.cashflow.server.model.Currency;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

@SuppressWarnings({"SqlDialectInspection"})
public class ImportFromAccounting {

    public static void main(String[] args) throws Exception {
        final Properties connectionProperties = new Properties();
        connectionProperties.put("user", "sa");
        connectionProperties.put("password", "sa");

        final String oldURL = "jdbc:h2:file:C:\\data\\accounting";
        final String newURL = "jdbc:h2:file:C:\\data\\cashflow";

        final Connection connectionOld = DriverManager.getConnection(oldURL, connectionProperties);
        System.out.println("Old database connected!");

        final Connection connectionNew = DriverManager.getConnection(newURL, connectionProperties);
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
        final List<TransferGroup> groups = new ArrayList<>();
        final Map<Integer, Operation> operations = new HashMap<>();
        try (final PreparedStatement statement = connectionOld.prepareStatement(
                "select id, date, account_id, currency_id, category_id, info, " +
                        "exchangeRate, total, totalCurrency, moneyWas, moneyBecome, income from balance order by id")) {

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

                    final TransferGroup group = new TransferGroup();
                    if (resultSet.getBoolean("income")) {
                        group.operationTo = operation;
                    } else {
                        group.operationFrom = operation;
                    }
                    groups.add(group);

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

                operations.put(operation.getId(), operation);
            }
        }

        // some connection by hand because of old database problem
        // 159 - 160
        Transfer transfer = new Transfer();
        transfer.setTo(operations.get(160).getAccount());
        transfer.setAmount(operations.get(160).getAmount());
        operations.get(159).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 159).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 160).collect(Collectors.toList()));
        operations.remove(160);
        // 168-173
        transfer = new Transfer();
        transfer.setTo(operations.get(174).getAccount());
        transfer.setAmount(operations.get(174).getAmount());
        operations.get(168).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(174).getAccount());
        transfer.setAmount(operations.get(174).getAmount());
        operations.get(169).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(174).getAccount());
        transfer.setAmount(operations.get(174).getAmount());
        operations.get(170).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(174).getAccount());
        transfer.setAmount(operations.get(174).getAmount());
        operations.get(171).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(174).getAccount());
        transfer.setAmount(operations.get(174).getAmount());
        operations.get(172).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(174).getAccount());
        transfer.setAmount(operations.get(174).getAmount());
        operations.get(173).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 168).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 169).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 170).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 171).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 172).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 173).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 174).collect(Collectors.toList()));
        operations.remove(174);
        // 175-176
        transfer = new Transfer();
        transfer.setTo(operations.get(177).getAccount());
        transfer.setAmount(operations.get(177).getAmount());
        operations.get(175).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(177).getAccount());
        transfer.setAmount(operations.get(177).getAmount());
        operations.get(176).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 175).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 176).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 177).collect(Collectors.toList()));
        operations.remove(177);
        // 471-472
        transfer = new Transfer();
        transfer.setTo(operations.get(473).getAccount());
        transfer.setAmount(operations.get(473).getAmount());
        operations.get(471).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(473).getAccount());
        transfer.setAmount(operations.get(473).getAmount());
        operations.get(472).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 471).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 472).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 473).collect(Collectors.toList()));
        operations.remove(473);
        // 385-386
        transfer = new Transfer();
        transfer.setTo(operations.get(387).getAccount());
        transfer.setAmount(operations.get(387).getAmount());
        operations.get(385).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(387).getAccount());
        transfer.setAmount(operations.get(387).getAmount());
        operations.get(386).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 385).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 386).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 387).collect(Collectors.toList()));
        operations.remove(387);
        // 339
        transfer = new Transfer();
        transfer.setTo(operations.get(348).getAccount());
        transfer.setAmount(operations.get(348).getAmount());
        operations.get(339).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 339).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 348).collect(Collectors.toList()));
        operations.remove(348);
        // 192
        transfer = new Transfer();
        transfer.setTo(operations.get(201).getAccount());
        transfer.setAmount(operations.get(201).getAmount());
        operations.get(192).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 192).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 201).collect(Collectors.toList()));
        operations.remove(201);
        // 31
        transfer = new Transfer();
        transfer.setTo(operations.get(32).getAccount());
        transfer.setAmount(operations.get(32).getAmount());
        operations.get(31).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 31).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 32).collect(Collectors.toList()));
        operations.remove(32);
        // 228
        transfer = new Transfer();
        transfer.setTo(operations.get(229).getAccount());
        transfer.setAmount(operations.get(229).getAmount());
        operations.get(228).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 228).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 229).collect(Collectors.toList()));
        operations.remove(229);
        // 112
        transfer = new Transfer();
        transfer.setTo(operations.get(116).getAccount());
        transfer.setAmount(operations.get(116).getAmount());
        operations.get(112).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 112).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 116).collect(Collectors.toList()));
        operations.remove(116);
        // 1071
        transfer = new Transfer();
        transfer.setTo(operations.get(1079).getAccount());
        transfer.setAmount(operations.get(1079).getAmount());
        operations.get(1071).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 1071).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 1079).collect(Collectors.toList()));
        operations.remove(1079);
        // 902
        transfer = new Transfer();
        transfer.setTo(operations.get(910).getAccount());
        transfer.setAmount(operations.get(910).getAmount());
        operations.get(902).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 902).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 910).collect(Collectors.toList()));
        operations.remove(910);
        // 903
        transfer = new Transfer();
        transfer.setTo(operations.get(904).getAccount());
        transfer.setAmount(operations.get(904).getAmount());
        operations.get(903).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 903).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 904).collect(Collectors.toList()));
        operations.remove(904);
        // 905-906
        transfer = new Transfer();
        transfer.setTo(operations.get(907).getAccount());
        transfer.setAmount(operations.get(907).getAmount());
        operations.get(905).setTransfer(transfer);
        transfer = new Transfer();
        transfer.setTo(operations.get(907).getAccount());
        transfer.setAmount(operations.get(907).getAmount());
        operations.get(906).setTransfer(transfer);
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 905).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationFrom != null && group.operationFrom.getId() == 906).collect(Collectors.toList()));
        groups.removeAll(groups.stream().filter(group -> group.operationTo != null && group.operationTo.getId() == 907).collect(Collectors.toList()));
        operations.remove(907);

        collapseDoubles(operations, groups, groups.stream().collect(Collectors.groupingBy(TransferGroup::getDate)));
        collapseDoubles(operations, groups, groups.stream().collect(Collectors.groupingBy(TransferGroup::getDate)));

        final long count = operations.values().stream().filter(operation -> operation.getType() == Operation.FlowType.TRANSFER && operation.getTransfer() == null).count();
        if (groups.size() > 0 || count > 0)
            throw new RuntimeException("ERROR. THERE ARE BROKEN TRANSFERS");

        System.out.println("Collected " + operations.size() + " operations");
        System.out.println("Data is prepared. Let's store it in the new database");

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
            for (Operation operation : operations.values()) {
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

        System.out.println("Inserting transfer information...");
        try (final PreparedStatement statement = connectionNew.prepareStatement(
                "insert into transfer (to_id, amount) values (?,?)", Statement.RETURN_GENERATED_KEYS)) {
            for (Operation operation : operations.values()) {
                final Transfer transferInfo = operation.getTransfer();
                if (transferInfo != null) {
                    statement.setInt(1, transferInfo.getTo().getId());
                    statement.setBigDecimal(2, transferInfo.getAmount());
                    statement.executeUpdate();

                    final ResultSet generatedKeys = statement.getGeneratedKeys();
                    generatedKeys.next();
                    transferInfo.setId(generatedKeys.getInt(1));
                }
            }
        }

        System.out.println("Inserting operations...");
        try (final PreparedStatement statement = connectionNew.prepareStatement(
                "insert into operation (id, date, account_id, currency_id, " +
                        "category_id, type, amount, moneyWas, moneyBecome, info, crosscurrency_id, transfer_id) " +
                        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (Operation operation : operations.values()) {
                statement.setInt(1, operation.getId());
                statement.setTimestamp(2, new Timestamp(operation.getDate().getTime()));
                statement.setInt(3, operation.getAccount().getId());
                statement.setInt(4, operation.getCurrency().getId());
                statement.setInt(5, operation.getCategory().getId());
                statement.setInt(6, operation.getType().ordinal());
                statement.setBigDecimal(7, operation.getAmount());
                statement.setBigDecimal(8, operation.getMoneyWas() == null ? BigDecimal.ZERO : operation.getMoneyWas());
                statement.setBigDecimal(9, operation.getMoneyBecome() == null ? BigDecimal.ZERO : operation.getMoneyBecome());
                statement.setString(10, operation.getInfo());
                statement.setObject(11, operation.getCrossCurrency() == null ? null : operation.getCrossCurrency().getId());
                statement.setObject(12, operation.getTransfer() == null ? null : operation.getTransfer().getId());
                statement.executeUpdate();
            }
        }
    }

    public static void collapseDoubles(Map<Integer, Operation> operations, List<TransferGroup> groups, Map<Date, List<TransferGroup>> mapByDate) {
        mapByDate.values().stream().filter(groupList -> groupList.size() % 2 == 0).forEach(groupList -> {
            if (groupList.get(0).operationFrom != null && groupList.get(1).operationTo != null) {
                final Transfer transfer = new Transfer();
                transfer.setTo(groupList.get(1).operationTo.getAccount());
                transfer.setAmount(groupList.get(1).operationTo.getAmount());
                operations.get(groupList.get(0).operationFrom.getId()).setTransfer(transfer);

                operations.remove(groupList.get(1).operationTo.getId());

                groups.remove(groupList.get(0));
                groups.remove(groupList.get(1));
            }
        });
    }

    public static class TransferGroup {

        public Operation operationFrom;
        public Operation operationTo;

        public Date getDate() {
            if (operationFrom == null)
                return operationTo.getDate();
            else
                return operationFrom.getDate();
        }

        @Override
        public String toString() {
            return "TransferGroup{" +
                    "operationFrom=" + (operationFrom != null ? operationFrom.getAmount().toString() : "null") +
                    ", operationTo=" + (operationTo != null ? operationTo.getAmount().toString() : "null") +
                    '}';
        }
    }
}
