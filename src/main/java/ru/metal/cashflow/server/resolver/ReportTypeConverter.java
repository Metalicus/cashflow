package ru.metal.cashflow.server.resolver;

import ru.metal.cashflow.server.model.report.ReportType;

import java.beans.PropertyEditorSupport;

/**
 * Converter for enum ReportType
 *
 * @see ru.metal.cashflow.server.model.report.ReportType
 */
public class ReportTypeConverter extends PropertyEditorSupport {

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
        setValue(ReportType.valueOf(text.toUpperCase()));
    }
}
