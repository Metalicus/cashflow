package ru.metal.cashflow.server.model.report;

/**
 * Abstract class for all reports
 */
public abstract class Report {

    /**
     * @return type of the report
     * @see ReportType
     */
    public abstract ReportType getReportType();

}
