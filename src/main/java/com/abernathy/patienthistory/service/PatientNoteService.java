package com.abernathy.patienthistory.service;

import com.abernathy.patienthistory.domain.PatientNote;
import com.abernathy.patienthistory.repository.PatientNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.List;

@Service
public class PatientNoteService {

    @Autowired
    private PatientNoteRepository repository;

    //Methods to serve Front End requests

    /**
     * Method to populate Model for frontend
     * Obtains all elements of this type from repository and adds to model
     * Then returns redirect to list url
     *
     * @param model Model object to hold data loaded from repo
     * @return redirect url String
     */
    public String home(Model model)
    {
        model.addAttribute("patientNotes", repository.findAll());
        return "patientNote/list";
    }

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
        model.addAttribute("currentPatientNote", e);
        return "patientNote/view";
    }

    /**
     * Method to populate View Patient Note page
     * Obtains PatientNote with specific ID from repository and adds to model
     * Then returns redirect to view url
     *
     * @param id    id parameter of DomainElement
     * @param model Model object to hold data loaded from repo
     * @return url String
     */
    public String viewByPatientId(int id, Model model) {
        model.addAttribute("thisPatientNotes", repository.findAllByPatId(id));
        return "patientNote/viewall";
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
            return "redirect:/patient/note/list";
        }
        return "patientNote/add";
    }

    /**
     * Method to get redirect for form to update existing PatientNote
     * Verifies that privided ID does match a note in the repo
     * Then returns url to update form
     *
     * @param id PatientNote's ID value
     * @param model Model object
     * @return url string
     */
    public String showUpdateForm(String id, Model model) {
        PatientNote e = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid PatientNote id:" + id));
        model.addAttribute("patientNote", e);
        return "patientNote/update";
    }

    /**
     * Method to validate provided PatientNote
     * Updates existing note in repo if valid & updates model
     * Returns to update form if not valid
     *
     * @param id PatientNote's ID value
     * @param e PatientNote with updated fields
     * @param result BindingResult for validation
     * @param model Model object
     * @return url string
     */
    public String update(String id, PatientNote e,
                         BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "patientNote/update";
        }

        e.setId(id);
        repository.save(e);
        model.addAttribute("patientNotes", repository.findAll());
        return "redirect:/patient/note/list";
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
        List<PatientNote> notes = repository.findAllByPatId(patId);
        if (notes.size() > 0) {
            // Notes found for provided patient ID
            return new ResponseEntity<String>(notes.toString(), new HttpHeaders(), HttpStatus.OK);
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

    /**
     * Method to validate provided PatientNote received via put request
     * Updates existing note in repo if valid & updates model
     *
     * @param e PatientNote with updated fields
     * @param result BindingResult for validation
     * @param model Model object
     * @return ResponseEntity JSON of updated element and 200 if valid,
     *         ResponseEntity JSON of requested update and 400 if invalid,
     *         ResponseEntity JSON of requested update and 404 if ID not found in database,
     */
    public ResponseEntity<String> updateFromApi(PatientNote e,
                                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            return new ResponseEntity<String>(e.toString(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
        }

        try {
            repository.findById(e.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid PatientNote Id:" + e.getId()));
        }
        catch (IllegalArgumentException error) {
            return new ResponseEntity<String>(e.toString(), new HttpHeaders(), HttpStatus.NOT_FOUND);
        }

        repository.save(e);
        model.addAttribute("patientNotes", repository.findAll());
        return new ResponseEntity<String>(e.toString(), new HttpHeaders(), HttpStatus.OK);
    }

    //Methods to serve RETROFIT API requests
    /**
     * Method to generate ResponseEntity for PatientNote get requests received via API
     *
     * @param patId Patient ID
     * @return List<PatientNote> of PatientNotes for requested Patient ID
     */
    public List<PatientNote> getFromApiByPatientIdRetro(int patId) {
        return repository.findAllByPatId(patId);
    }

}
