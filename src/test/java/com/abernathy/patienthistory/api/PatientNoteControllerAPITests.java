package com.abernathy.patienthistory.api;

import com.abernathy.patienthistory.domain.PatientNote;
import com.abernathy.patienthistory.repository.PatientNoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class PatientNoteControllerAPITests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private static PatientNoteRepository patientNoteRepository;

    @Test
    public void patientNoteControllerAPIAddsEntry() throws Exception {

        PatientNote patientNote = new PatientNote();
        patientNote.setPatId("1");
        patientNote.setNote("Test Note");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(patientNote);

        MvcResult mvcResult = mockMvc.perform(
                post("/patient/note/api/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is added to DB, and we get created response (201)
        assertTrue(mvcResult.getResponse().getStatus() == 201);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).save(any(PatientNote.class));
    }

    @Test
    public void patientNoteControllerAPIWillNotAddInvalidEntry() throws Exception {

        PatientNote patientNote = new PatientNote();
        patientNote.setPatId("1");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(patientNote);

        MvcResult mvcResult = mockMvc.perform(
                post("/patient/note/api/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is not added to DB, and we get failed response (400)
        assertTrue(mvcResult.getResponse().getStatus() == 400);
        Mockito.verify(patientNoteRepository, Mockito.times(0)).save(any(PatientNote.class));
    }

}
