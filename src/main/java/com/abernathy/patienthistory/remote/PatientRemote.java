package com.abernathy.patienthistory.remote;

import com.abernathy.patienthistory.remote.interfaces.PatientRetro;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.Map;

@Service
public class PatientRemote {

    @Value("${docker.patient.ip}")
    private String ip = "127.0.0.1";

    @Value("${docker.patient.port}")
    private String port = "8080";

    private Logger logger = LoggerFactory.getLogger(PatientRemote.class);

    private Gson gson = new GsonBuilder().setLenient().create();

    /**
     * Method to get index of patients from patient database via api call
     * For use in add patient note front end ui
     *
     * @return Map of Patient ID to Patient Names
     */
    public Map<Integer, String> getPatientIndex() {
        logger.info("getPatientIndex called");

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + ":" + port + "/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient.build())
                .build();

        PatientRetro patientService = retrofit.create(PatientRetro.class);

        Call<Map<Integer, String>> callSync = patientService.getPatientIndex();

        try {
            Response<Map<Integer, String>> response = callSync.execute();
            Map<Integer, String> value = response.body();
            System.out.println("getPatientIndex external call completed");
            System.out.println(response);
            System.out.println(response.body());
            logger.debug("getPatientIndex external call completed");
            return value;
        } catch (Exception e) {
            System.out.println("getPatientIndex external call failed: " + e);
            logger.error("getPatientIndex external call failed: " + e);
            return null;
        }
    }

}
