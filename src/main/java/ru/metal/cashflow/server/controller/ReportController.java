package ru.metal.cashflow.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.metal.cashflow.server.exception.CFException;
import ru.metal.cashflow.server.model.report.Report;
import ru.metal.cashflow.server.model.report.ReportType;
import ru.metal.cashflow.server.request.ReportRequest;
import ru.metal.cashflow.server.service.ReportService;

@RestController
@RequestMapping(value = "report", produces = RestCRUDController.MEDIA_TYPE)
public class ReportController {

    @Autowired
    private ReportService service;

    @RequestMapping(value = "/{type}", method = RequestMethod.GET)
    public Report get(@PathVariable ReportType type, ReportRequest request) throws CFException {
        return service.buildReport(type, request);
    }
}
