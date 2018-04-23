package com.ifood.service.report;

import com.ifood.domain.ConnectionReport;

import java.time.LocalDateTime;

public interface ReportService {
    ConnectionReport fetchReport(String restaurantCode, LocalDateTime startDate, LocalDateTime endDate);
}
