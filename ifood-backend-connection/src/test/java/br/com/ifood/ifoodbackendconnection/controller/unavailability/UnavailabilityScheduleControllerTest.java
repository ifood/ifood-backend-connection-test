package br.com.ifood.ifoodbackendconnection.controller.unavailability;

import br.com.ifood.ifoodbackendconnection.service.UnavailabilityScheduleService;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UnavailabilityScheduleControllerTest {

    @InjectMocks
    private UnavailabilityScheduleController unavailabilityScheduleController;

    @Mock
    private UnavailabilityScheduleService unavailabilityScheduleService;

    private String scheduleCode = "bbb472a3-0544-4e68-a3eb-3740d42ece7d";

    @Test
    public void shouldDeleteUnavailabilitySchedule() {
        ResponseEntity responseEntity = unavailabilityScheduleController.deleteUnavailabilitySchedule(scheduleCode);

        verify(this.unavailabilityScheduleService, times(1)).deleteScheduleBy(scheduleCode);

        assertThat(responseEntity.getStatusCode(), Matchers.is(HttpStatus.NO_CONTENT));
    }

    @Test
    public void shouldReturnNotFoundWhenScheduleCodeToBeDeleteDoesNotFound() {
        doThrow(IllegalArgumentException.class).when(this.unavailabilityScheduleService).deleteScheduleBy(scheduleCode);

        ResponseEntity responseEntity = unavailabilityScheduleController.deleteUnavailabilitySchedule(scheduleCode);

        verify(this.unavailabilityScheduleService, times(1)).deleteScheduleBy(scheduleCode);

        assertThat(responseEntity.getStatusCode(), Matchers.is(HttpStatus.NOT_FOUND));
        assertThat(responseEntity.hasBody(), Matchers.is(Boolean.FALSE));
    }
}
