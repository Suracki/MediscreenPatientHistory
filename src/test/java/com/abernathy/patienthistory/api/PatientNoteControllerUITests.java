package com.abernathy.patienthistory.api;

import com.abernathy.patienthistory.domain.PatientNote;
import com.abernathy.patienthistory.repository.PatientNoteRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties")
public class PatientNoteControllerUITests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private static PatientNoteRepository patientNoteRepository;

    @Test
    public void patientNoteControllerGetAddNoteForm() throws Exception {

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/add").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertTrue(mvcResult.getResponse().getStatus() == 200);
    }

    @Test
    public void patientNoteControllerPostValidateAddsEntry() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                post("/patient/note/validate")
                        .param("patId", "1")
                        .param("note", "test note")
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is added to DB and we are redirected (302)
        assertTrue(mvcResult.getResponse().getStatus() == 302);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).save(any(PatientNote.class));
    }

    @Test
    public void patientNoteControllerPostValidateDoesNotAddInvalidEntry() throws Exception {

        MvcResult mvcResult = mockMvc.perform(
                post("/patient/note/validate")
                        .param("note", "test note")
                        .accept(MediaType.ALL)).andReturn();

        //Verify no entry is added to DB and we remain on add form (200)
        assertTrue(mvcResult.getResponse().getStatus() == 200);
        Mockito.verify(patientNoteRepository, Mockito.times(0)).save(any(PatientNote.class));
    }

    @Test
    public void patientNoteControllerGetViewPatientNotePage() throws Exception {

        when(patientNoteRepository.findById("TESTID")).thenReturn(java.util.Optional.of(new PatientNote()));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/view/TESTID").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertTrue(mvcResult.getResponse().getStatus() == 200);
    }

    @Test
    public void patientNoteControllerGetViewAllPatientNotePage() throws Exception {

        when(patientNoteRepository.findAllByPatId(1)).thenReturn(new ArrayList<>());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/viewall/1").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertTrue(mvcResult.getResponse().getStatus() == 200);
    }

    @Test
    public void patientNoteControllerGetUpdatePatientForm() throws Exception {

        when(patientNoteRepository.findById("NOTEID")).thenReturn(java.util.Optional.of(new PatientNote()));
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/patient/note/update/NOTEID").accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

        assertTrue(mvcResult.getResponse().getStatus() == 200);
    }

    @Test
    public void patientNoteControllerPostUpdatesEntry() throws Exception {

        when(patientNoteRepository.findById("NOTEID")).thenReturn(java.util.Optional.of(new PatientNote()));
        MvcResult mvcResult = mockMvc.perform(
                post("/patient/note/update/NOTEID")
                        .param("patientNoteId", "NOTEID")
                        .param("patId", "1")
                        .param("note", "testnote")
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is updated in DB and we are redirected (302)
        assertTrue(mvcResult.getResponse().getStatus() == 302);
        Mockito.verify(patientNoteRepository, Mockito.times(1)).save(any(PatientNote.class));
    }

    @Test
    public void patientNoteControllerPostDoesNotUpdateInvalidEntry() throws Exception {

        when(patientNoteRepository.findById("NOTEID")).thenReturn(java.util.Optional.of(new PatientNote()));
        MvcResult mvcResult = mockMvc.perform(
                post("/patient/note/update/NOTEID")
                        .param("patientNoteId", "NOTEID")
                        .param("patId", "BADINPUT")
                        .param("note", "testnote")
                        .accept(MediaType.ALL)).andReturn();

        //Verify entry is not updated in DB and we remain on form (200)
        assertTrue(mvcResult.getResponse().getStatus() == 200);
        Mockito.verify(patientNoteRepository, Mockito.times(0)).save(any(PatientNote.class));
    }

}
