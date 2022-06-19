package com.abernathy.patienthistory.remote.interfaces;

import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.Map;

@Service
public interface PatientRetro {

    @GET("/patient/api/retro/get/index")
    public Call<Map<Integer, String>> getPatientIndex();

}
