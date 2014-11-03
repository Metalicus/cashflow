package liquibase.ext.logging;


import liquibase.logging.core.AbstractLogger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Stupid class, to enable liquibase logging.
 * Liquibase finds this class by itself by doing a custom component scan
 */
@SuppressWarnings("UnusedDeclaration")
public class LiquibaseLogger extends AbstractLogger {

    private Log logger;

    @Override
    public void setName(String name) {
        logger = LogFactory.getLog(name);
    }

    @Override
    public void severe(String message) {
        logger.error(message);
    }

    @Override
    public void severe(String message, Throwable e) {
        logger.error(message, e);
    }

    @Override
    public void warning(String message) {
        logger.warn(message);
    }

    @Override
    public void warning(String message, Throwable e) {
        logger.warn(message, e);
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info(String message, Throwable e) {
        logger.info(message, e);
    }

    @Override
    public void debug(String message) {
        logger.debug(message);
    }

    @Override
    public void debug(String message, Throwable e) {
        logger.debug(message, e);
    }

    @Override
    public void setLogLevel(String logLevel, String logFile) {
    }

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}