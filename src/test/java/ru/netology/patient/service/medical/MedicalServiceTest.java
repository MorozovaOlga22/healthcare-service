package ru.netology.patient.service.medical;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.netology.patient.entity.BloodPressure;
import ru.netology.patient.entity.HealthInfo;
import ru.netology.patient.entity.PatientInfo;
import ru.netology.patient.repository.PatientInfoRepository;
import ru.netology.patient.service.alert.SendAlertService;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MedicalServiceTest {
    private final SendAlertService sendAlertService = Mockito.mock(SendAlertService.class);
    private final MedicalService medicalService = createMock();

    @Test
    void checkBloodPressureNormal() {
        // given:
        final String patientId = "1";
        final BloodPressure bloodPressure = new BloodPressure(110, 70);

        // when:
        medicalService.checkBloodPressure(patientId, bloodPressure);

        // then:
        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }

    @Test
    void checkBloodPressureChanged() {
        // given:
        final String patientId = "1";
        final BloodPressure bloodPressure = new BloodPressure(120, 70);
        final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        final String expectedMessage = "Warning, patient with id: 1, need help";

        // when:
        medicalService.checkBloodPressure(patientId, bloodPressure);

        // then:
        Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals(expectedMessage, argumentCaptor.getValue());
    }

    @Test
    void checkTemperatureNormal() {
        // given:
        final String patientId = "1";
        final BigDecimal currentTemperature = new BigDecimal("36.6");

        // when:
        medicalService.checkTemperature(patientId, currentTemperature);

        // then:
        Mockito.verify(sendAlertService, Mockito.never()).send(Mockito.anyString());
    }

    //У метода checkTemperature очень странная логика работы...
    @Test
    void checkTemperatureChanged() {
        // given:
        final String patientId = "1";
        final BigDecimal currentTemperature = new BigDecimal("34.6");
        final ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        final String expectedMessage = "Warning, patient with id: 1, need help";

        // when:
        medicalService.checkTemperature(patientId, currentTemperature);

        // then:
        Mockito.verify(sendAlertService, Mockito.only()).send(Mockito.anyString());
        Mockito.verify(sendAlertService).send(argumentCaptor.capture());
        assertEquals(expectedMessage, argumentCaptor.getValue());
    }

    private MedicalService createMock() {
        final PatientInfoRepository patientInfoRepository = Mockito.mock(PatientInfoRepository.class);
        final BloodPressure bloodPressure = new BloodPressure(110, 70);
        final BigDecimal normalTemperature = new BigDecimal("36.6");
        final HealthInfo healthInfo = new HealthInfo(normalTemperature, bloodPressure);
        final PatientInfo patientInfo = new PatientInfo("1", null, null, null, healthInfo);

        Mockito.when(patientInfoRepository.getById("1")).thenReturn(patientInfo);

        return new MedicalServiceImpl(patientInfoRepository, sendAlertService);
    }
}