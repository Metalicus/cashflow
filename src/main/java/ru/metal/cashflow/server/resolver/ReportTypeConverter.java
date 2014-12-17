package ru.metal.cashflow.server.resolver;

import org.springframework.core.convert.converter.Converter;
import ru.metal.cashflow.server.model.report.ReportType;

/**
 * Converter for enum ReportType
 *
 * @see ru.metal.cashflow.server.model.report.ReportType
 */
public class ReportTypeConverter implements Converter<String, ReportType> {

    @Override
    public ReportType convert(String source) {
        return ReportType.valueOf(source.toUpperCase());
    }
}
