package be.cegeka.batchers.taxservice.stubwebservice;

import be.cegeka.batchers.taxcalculator.to.TaxServiceResponse;
import be.cegeka.batchers.taxcalculator.to.TaxTo;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TaxControllerTest {

    @Mock
    private TextFileTaxLogger taxLogger;

    @Mock
    private SpecialEmployeesService specialEmployeesServiceMock;

    @InjectMocks
    private TaxController taxController;

    private TaxTo taxTo;
    private Long employeeId;

    @Before
    public void setup() {
        employeeId = 1L;
        taxTo = new TaxTo(employeeId, 123.0);
    }

    @Test
    public void givenValidTaxTo_whenSubmitTaxForm_ThenALogLineIsCreatedWithStatusOk() throws JsonProcessingException {
        taxController.submitTaxForm(taxTo);

        verify(taxLogger, times(1)).log(taxTo, "OK");
    }

    @Test
    public void givenBlacklistedEmployee_whenSubmitTaxForm_ThenALogLineIsCreatedWithStatusNotOk() throws JsonProcessingException {
        when(specialEmployeesServiceMock.isEmployeeBlacklisted(1L)).thenReturn(true);

        taxController.submitTaxForm(taxTo);

        verify(taxLogger, times(1)).log(taxTo, "FAILURE");
    }

    @Test
    public void givenBlacklistEmployee_whenSubmitTaxForm_thenResponseFails() throws JsonProcessingException {
        when(specialEmployeesServiceMock.isEmployeeBlacklisted(employeeId)).thenReturn(true);

        ResponseEntity<TaxServiceResponse> response = taxController.submitTaxForm(taxTo);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getStatus()).isEqualTo(TaxController.RESPONSE_BODY_FAIL);
    }

    @Test
    public void givenEmployeeWithTax_whenSubmitTaxForm_thenItSleepsIfNecessary() throws InterruptedException, JsonProcessingException {

        taxController.submitTaxForm(taxTo);

        verify(specialEmployeesServiceMock).sleepIfNecessary(employeeId);
    }

    @Test
    public void givenBlacklistEmployee_whenSubmitTaxForm_thenDoNotTryToTimeout() throws JsonProcessingException {
        when(specialEmployeesServiceMock.isEmployeeBlacklisted(employeeId)).thenReturn(true);

        taxController.submitTaxForm(taxTo);

        verify(specialEmployeesServiceMock, times(0)).sleepIfNecessary(employeeId);
    }

    @Test
    public void testResetSpecialEmployeesService_isReset() throws Exception {
        taxController.resetSpecialEmployeesService();

        verify(specialEmployeesServiceMock).reset();
    }
}