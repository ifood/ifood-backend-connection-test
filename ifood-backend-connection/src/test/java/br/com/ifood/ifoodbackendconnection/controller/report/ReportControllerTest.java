package br.com.ifood.ifoodbackendconnection.controller.report;

import br.com.ifood.ifoodbackendconnection.domain.ErrorList;
import br.com.ifood.ifoodbackendconnection.domain.RestaurantStatus;
import br.com.ifood.ifoodbackendconnection.domain.RestaurantStatusBuilder;
import br.com.ifood.ifoodbackendconnection.service.ReportService;
import br.com.ifood.ifoodbackendconnection.utilities.DateFormatter;
import br.com.ifood.ifoodbackendconnection.utilities.Either;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ReportControllerTest {

    @InjectMocks
    private ReportController reportController;

    @Mock
    private ReportService reportService;

    @Test
    public void shouldFetchReport() {
        String restaurantCode = "aaa472a3-0544-4e68-a3eb-3740d42ece7d";
        String startDate = "2019-01-23T20:15";
        String endDate = "2019-01-23T20:30";

        Either<ErrorList, RestaurantStatus> restaurantStatusEither = Either.right(new RestaurantStatusBuilder().build());

        when(reportService.fetchReport(restaurantCode, DateFormatter.format(startDate), DateFormatter.format(endDate))).thenReturn(restaurantStatusEither);

        ResponseEntity restaurantStatus = reportController.fetchReport(restaurantCode, startDate, endDate);

        assertThat(restaurantStatus.getStatusCode(), Matchers.is(HttpStatus.OK));
    }
}
