package com.abernathy.patienthistory.controllers;

import com.abernathy.patienthistory.domain.PatientNote;
import com.abernathy.patienthistory.service.PatientNoteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@Controller
public class PatientNoteController {

    @Autowired
    PatientNoteService patientNoteService;

    private static final Logger logger = LogManager.getLogger("PatientNoteController");

    //Endpoints for serving front end
    @GetMapping("/patient/note/add")
    public String addPatientNote(PatientNote patientNote) {
        logger.info("User connected to /patient/note/add endpoint");
        return patientNoteService.addForm(patientNote);
    }

    @PostMapping("/patient/note/validate")
    public String validate(@Valid PatientNote patientNote, BindingResult result, Model model) {
        logger.info("User connected to /patient/note/validate endpoint");
        return patientNoteService.validate(patientNote,result,model);
    }

    @GetMapping("/patient/note/view/{id}")
    public String viewPatientNote(@PathVariable("id") String id, Model model) {
        logger.info("User connected to /patient/note/view endpoint with id " + id);
        return patientNoteService.view(id, model);
    }

    //Endpoints for serving REST API
    @PostMapping("/patient/note/api/add")
    public ResponseEntity<String> addPatientNoteApi(@Valid @RequestBody PatientNote patientNote, BindingResult result, Model model) {
        logger.info("User connected to /patient/note/api/add endpoint");
        return patientNoteService.addFromApi(patientNote, result, model);
    }

    @GetMapping("/patient/note/api/get/{id}")
    public ResponseEntity<String> getPatientNoteApi(@PathVariable("id") String id, Model model) {
        logger.info("User connected to /patient/note/get endpoint with id " + id);
        return patientNoteService.getFromApi(id, model);
    }

    @GetMapping("/patient/note/api/getbypatient/{patId}")
    public ResponseEntity<String> getAllOnePatientNotesApi(@PathVariable("patId") int patId, Model model) {
        logger.info("User connected to /patient/note/api/getbypatient endpoint with id " + patId);
        return patientNoteService.getFromApiByPatientId(patId, model);
    }

}
