package ru.metal.cashflow.server.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.business.Category;
import ru.metal.cashflow.server.model.business.Currency;
import ru.metal.cashflow.server.model.business.Operation;
import ru.metal.cashflow.server.model.report.MonthlyBalance;
import ru.metal.cashflow.server.model.report.Report;
import ru.metal.cashflow.server.model.report.ReportType;
import ru.metal.cashflow.server.repository.OperationRepository;
import ru.metal.cashflow.server.request.ReportRequest;
import ru.metal.cashflow.server.utils.DateUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Service for creating all reports
 */
@Service
@Transactional(rollbackFor = CFException.class, readOnly = true)
public class ReportService {

    private static final Log logger = LogFactory.getLog(ReportService.class);

    @Autowired
    OperationRepository operationRepository;
    @Autowired
    CurrencyService currencyService;
    @Autowired
    CategoryService categoryService;

    /**
     * Build report by type and request
     *
     * @param type    type of report
     * @param request additional parameters
     * @return report
     */
    public Report buildReport(ReportType type, ReportRequest request) throws CFException {
        switch (type) {
            case MONTHLY_BALANCE:
                return buildMonthlyReport(request.getInt("month"), request.getInt("year"));
            default:
                logger.error("Unknown report type!");
                throw new CFException("Unknown report type!");
        }
    }

    /**
     * Build Monthly Balance report
     *
     * @param month for month
     * @param year  and for year
     * @return report
     */
    public MonthlyBalance buildMonthlyReport(int month, int year) {
        final Date fromDate = DateUtils.minusDay(DateUtils.create(year, month, 1), 1);
        final Date toDate = DateUtils.plusMonth(fromDate, 1);

        final List<Object[]> finded = new ArrayList<>();
        finded.addAll(operationRepository.findMonthlyReport(fromDate, toDate));
        finded.addAll(operationRepository.findMonthlyCrossCurrencyReport(fromDate, toDate));

        final MonthlyBalance report = new MonthlyBalance();

        Category category;
        Currency currency;
        for (Object[] objects : finded) {
            currency = currencyService.get((Integer) objects[1]);
            category = categoryService.get((Integer) objects[2]);
            report.addRow(category, currency, (Operation.FlowType) objects[3], (BigDecimal) objects[0]);
        }

        return report;
    }
}
