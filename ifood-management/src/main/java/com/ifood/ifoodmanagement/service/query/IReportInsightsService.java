package com.ifood.ifoodmanagement.service.query;

import com.ifood.ifoodmanagement.domain.ReportItem;

public interface IReportInsightsService {

    public ReportItem fetchRestaurantReportInsights(String code);
}
