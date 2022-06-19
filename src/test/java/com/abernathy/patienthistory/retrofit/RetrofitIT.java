package com.abernathy.patienthistory.retrofit;

import com.abernathy.patienthistory.remote.PatientRemote;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class RetrofitIT {

    @Test
    public void patientRemoteCanGetPatientIndex() {

        PatientRemote patientRemote = new PatientRemote();
        Map<Integer, String> index = patientRemote.getPatientIndex();

        System.out.println("Retrieved patient index: " + index);
        assertTrue(index != null);

    }

}

