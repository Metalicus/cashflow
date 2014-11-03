package liquibase;

import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffGeneratorFactory;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.SnapshotControl;
import liquibase.snapshot.SnapshotGeneratorFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Create snapshot from DB
 */
public class CreateSnapshot {

    public static void main(String[] args) {
        try {
            final Properties connectionProperties = new Properties();
            connectionProperties.put("user", "sa");
            connectionProperties.put("password", "sa");

            final Connection connection = DriverManager.getConnection(
                    "jdbc:h2:file:C:\\data\\cashflow", connectionProperties
            );

            final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            final SnapshotControl snapshotControl = new SnapshotControl(database, "");
            final DatabaseSnapshot referenceSnapshot = SnapshotGeneratorFactory.getInstance().createSnapshot(new CatalogAndSchema("", ""), database, snapshotControl);
            final DiffResult diffResult = DiffGeneratorFactory.getInstance().compare(referenceSnapshot, null, new CompareControl(new CompareControl.SchemaComparison[]{new CompareControl.SchemaComparison(new CatalogAndSchema("", ""), new CatalogAndSchema("", ""))}, ""));
            final DiffOutputControl diffOutputConfig = new DiffOutputControl(false, false, false).addIncludedSchema(new CatalogAndSchema("", ""));
            new DiffToChangeLog(diffResult, diffOutputConfig).print(System.out);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
