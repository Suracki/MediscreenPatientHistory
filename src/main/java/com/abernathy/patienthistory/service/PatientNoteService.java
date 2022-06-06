package com.abernathy.patienthistory.service;

import com.abernathy.patienthistory.domain.DomainElement;
import com.abernathy.patienthistory.domain.PatientNote;
import com.abernathy.patienthistory.repository.PatientNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PatientNoteService {

    @Autowired
    private PatientNoteRepository repository;

    //Methods to serve Front End requests

    /**
     * Method to populate View Patient Note page
     * Obtains PatientNote with specific ID from repository and adds to model
     * Then returns redirect to view url
     *
     * @param id    id parameter of DomainElement
     * @param model Model object to hold data loaded from repo
     * @return url String
     */
    public String view(String id, Model model) {
        PatientNote e = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid PatientNote Id:" + id));
        model.addAttribute("currentPatient", e);
        return "patientNote/view";
    }

    /**
     * Method to get redirect for form to add a new element
     *
     * @param e PatientNote object of type to be added
     * @return url String
     */
    public String addForm(PatientNote e) {
        return "patientNote/add";
    }

    /**
     * Method to validate provided PatientNote
     * Adds PatientNote to repository if valid & updates model
     * Returns to form if any errors found
     *
     * @param e      PatientNote object to be added
     * @param result BindingResult for validation
     * @param model  Model object
     * @return url String
     */
    public String validate(@Valid PatientNote e, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            repository.save(e);
            model.addAttribute("PatientNotes", repository.findAll());
            return "redirect:/patientNote/list";
        }
        return "patientNote/add";
    }

    //Methods to serve REST API requests

    /**
     * Method to generate ResponseEntity for PatientNote get requests received via API
     *
     * @param id    id parameter of PatientNote
     * @param model Model object to hold data loaded from repo
     * @return ResponseEntity JSON of requested PatientNote and 200 if valid, 404 if invalid
     */
    public ResponseEntity<String> getFromApi(String id, Model model) {
        try {
            PatientNote e = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid PatientNote Id:" + id));
            return new ResponseEntity<String>(e.toString(), new HttpHeaders(), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<String>("Id " + id + " not found", new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to generate ResponseEntity for PatientNote get requests received via API
     *
     * @param patId Patient ID
     * @param model Model object to hold data loaded from repo
     * @return ResponseEntity JSON of PatientNotes for requested Patient ID and 200 if valid, 404 if no note found
     */
    public ResponseEntity<String> getFromApiByPatientId(int patId, Model model) {
        List<Optional<PatientNote>> notes = repository.findAllByPatId(patId);
        List<PatientNote> patientNotes = new ArrayList<>();
        for (Optional<PatientNote> patientNote : notes) {
            if (patientNote.isPresent()) {
                patientNotes.add(patientNote.get());
            }
        }
        if (patientNotes.size() > 0) {
            // Notes found for provided patient ID
            return new ResponseEntity<String>(patientNotes.toString(), new HttpHeaders(), HttpStatus.OK);
        }
        else {
            // No notes for this patient in database
            return new ResponseEntity<String>("Patient " + patId + " has no notes", new HttpHeaders(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Method to validate provided PatientNote received via API post request
     * Adds PatientNote to repository if valid & updates model
     *
     * @param e      PatientNote object to be added
     * @param result BindingResult for validation
     * @param model  Model object
     * @return ResponseEntity JSON of added PatientNote and 201 if valid, 400 if invalid
     */
    public ResponseEntity<String> addFromApi(PatientNote e, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            repository.save(e);
            model.addAttribute("PatientNotes", repository.findAll());
            return new ResponseEntity<String>(e.toString(), new HttpHeaders(), HttpStatus.CREATED);
        }

        return new ResponseEntity<String>("Failed to add new entry", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
