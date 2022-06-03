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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

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

    @Test
    public void patientNoteControllerAPIGetsEntry() throws Exception {

        //Create mock patient note
        PatientNote patientNote = new PatientNote();
        patientNote.setPatientNoteId("TESTID");
        patientNote.setPatId("1");
        patientNote.setNote("TEST NOTE");

        //If our service works and asks the repo for patient note with id TESTID, return our mock patient note
        when(patientNoteRepository.findById("TESTID")).thenReturn(java.util.Optional.of(patientNote));

        //Attempt to retrieve patient note
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/api/get/TESTID")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        //Verify entry is retrieved from DB, and we get success response (200)
        assertTrue(mvcResult.getResponse().getStatus() == 200);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).findById("TESTID");

    }

    @Test
    public void patientNoteControllerAPIDoesNotGetInvalidEntry() throws Exception {

        //Attempt to retrieve patient note
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/api/get/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        //Verify retrieval is attempted from DB, and we get Not Found response (404)
        assertTrue(mvcResult.getResponse().getStatus() == 404);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).findById("1");

    }

    @Test
    public void patientNoteControllerAPIGetsAllNotesForPatient() throws Exception {

        //Create mock patient notes
        List<PatientNote> notes = new ArrayList<>();
        notes.add(new PatientNote());
        notes.add(new PatientNote());

        //If our service works and asks the repo for notes from patient with id 1, return our mock patient notes
        when(patientNoteRepository.findAllByPatId(1)).thenReturn(notes);

        //Attempt to retrieve patient notes
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/api/getbypatient/1")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        //Verify entry is retrieved from DB, and we get success response (200)
        assertTrue(mvcResult.getResponse().getStatus() == 200);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).findAllByPatId(1);

    }

    @Test
    public void patientNoteControllerAPIInformsWhenPatientHasNoNotes() throws Exception {

        //Attempt to retrieve patient notes
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/api/getbypatient/7")
                .accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        //Verify retrieval is attempted from DB, and we get Not Found response (404)
        assertTrue(mvcResult.getResponse().getStatus() == 404);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).findAllByPatId(7);

    }

    @Test
    public void patientNoteControllerAPIUpdatesEntry() throws Exception {

        //Create mock note with valid data
        PatientNote note = new PatientNote();
        note.setPatientNoteId("NOTEID");
        note.setPatId("1");
        note.setNote("NOTE");


        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(note);

        //If our service accepts the PatientNote JSON and asks the repo for patient with id NOTEID, return a valid patient
        when(patientNoteRepository.findById("NOTEID")).thenReturn(java.util.Optional.of(note));

        //Attempt to update note
        MvcResult mvcResult = mockMvc.perform(
                put("/patient/note/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is saved to DB, and we get success response (200)
        assertTrue(mvcResult.getResponse().getStatus() == 200);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).save(any());

    }

    @Test
    public void patientNoteControllerAPIDoesNotUpdateWithInvalidData() throws Exception {

        //Create mock note with valid ID but invalid/missing data
        PatientNote note = new PatientNote();
        note.setPatientNoteId("NOTEID");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(note);

        //If our service accepts the Patient JSON and asks the repo for patient with id NOTEID, return a valid patient
        when(patientNoteRepository.findById("NOTEID")).thenReturn(java.util.Optional.of(note));

        //Attempt to update note
        MvcResult mvcResult = mockMvc.perform(
                put("/patient/note/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is not saved to DB, and we get bad request response (400)
        assertTrue(mvcResult.getResponse().getStatus() == 400);
        Mockito.verify(patientNoteRepository, Mockito.times(0)).save(any());

    }

    @Test
    public void patientNoteControllerAPIDoesNotUpdateWithInvalidID() throws Exception {

        //Create mock note with valid data
        PatientNote note = new PatientNote();
        note.setPatientNoteId("NOTEID");
        note.setPatId("1");
        note.setNote("NOTE");

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson=ow.writeValueAsString(note);

        //When our service accepts the PatientNote JSON and asks the repo for patient with id NOTEID
        //We want to simulate not finding a note, so we omit patientRepository thenReturn line

        //Attempt to update note
        MvcResult mvcResult = mockMvc.perform(
                put("/patient/note/api/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is not saved to DB, and we get not found response (404)
        assertTrue(mvcResult.getResponse().getStatus() == 404);
        Mockito.verify(patientNoteRepository, Mockito.times(0)).save(any());

    }

}
