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
import org.springframework.web.bind.annotation.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.validation.Valid;

@Controller
public class PatientNoteController {

    @Autowired
    PatientNoteService patientNoteService;

    private static final Logger logger = LogManager.getLogger("PatientNoteController");

    private Gson gson = new GsonBuilder().create();

    //Endpoints for serving front end

    /**
     * Mapping for GET
     *
     * Serves list notes page for Mediscreen app
     *
     * @param model Model
     * @return list notes homepage
     */
    @RequestMapping("/patient/note/list")
    public String home(Model model)
    {
        logger.info("User connected to /patient/note/list endpoint");
        return patientNoteService.home(model);
    }

    /**
     * Mapping for GET
     *
     * Serves add note page for Mediscreen app
     *
     * @param patientNote patient note object
     * @param model Model
     * @return homepage
     */
    @GetMapping("/patient/note/add")
    public String addPatientNote(PatientNote patientNote, Model model) {
        logger.info("User connected to /patient/note/add endpoint");
        return patientNoteService.addForm(patientNote, model);
    }

    /**
     * Mapping for POST
     *
     * Requests validation of PatientNote created via Add page
     *
     * @param patientNote patient note object
     * @param result BindingResult for validation
     * @param model Model
     * @return notes list page if successful, add note page if unsuccessful
     */
    @PostMapping("/patient/note/validate")
    public String validate(@Valid PatientNote patientNote, BindingResult result, Model model) {
        logger.info("User connected to /patient/note/validate endpoint");
        return patientNoteService.validate(patientNote,result,model);
    }

    /**
     * Mapping for GET
     *
     * Serves view note page for Mediscreen app
     *
     * @param id patient note id
     * @param model Model
     * @return view note page
     */
    @GetMapping("/patient/note/view/{id}")
    public String viewPatientNote(@PathVariable("id") String id, Model model) {
        logger.info("User connected to /patient/note/view endpoint with id " + id);
        return patientNoteService.view(id, model);
    }

    /**
     * Mapping for GET
     *
     * Serves view all notes for specific patient page for Mediscreen app
     *
     * @param id patient id
     * @param model Model
     * @return view all notes page
     */
    @GetMapping("/patient/note/viewall/{id}")
    public String viewAllOnePatientNotes(@PathVariable("id") int id, Model model) {
        logger.info("User connected to /patient/note/viewall endpoint with id " + id);
        return patientNoteService.viewByPatientId(id, model);
    }

    /**
     * Mapping for GET
     *
     * Serves update note page for Mediscreen app
     *
     * @param id patient note id
     * @param model Model
     * @return update note page
     */
    @GetMapping("/patient/note/update/{id}")
    public String showUpdateForm(@PathVariable("id") String id, Model model) {
        logger.info("User connected to /patient/note/update/ GET endpoint for patient with id " + id);
        return patientNoteService.showUpdateForm(id, model);
    }

    /**
     * Mapping for POST
     *
     * Requests validation of PatientNote updated via Update page
     *
     * @param id patient note id
     * @param patientNote patient note object
     * @param result BindingResult for validation
     * @param model Model
     * @return notes list page if successful, update note page if unsuccessful
     */
    @PostMapping("/patient/note/update/{id}")
    public String updateNote(@PathVariable("id") String id, @Valid PatientNote patientNote,
                                 BindingResult result, Model model) {
        logger.info("User connected to /patient/note/update/ POST endpoint for patient with id " + id);
        return patientNoteService.update(id, patientNote, result, model);
    }

    //Endpoints for serving REST API
    /**
     * Mapping for POST
     *
     * Returns:
     * HttpStatus.BAD_REQUEST if note cannot be added (eg invalid data)
     * Json string & HttpStatus.CREATED if successful
     *
     * @param patientNote note object to be added
     * @param result BindingResult for validation
     * @return Json string & HttpStatus.CREATED if successful
     */
    @PostMapping("/patient/note/api/add")
    public ResponseEntity<String> addPatientNoteApi(@Valid @RequestBody PatientNote patientNote, BindingResult result) {
        logger.info("User connected to /patient/note/api/add endpoint");
        return patientNoteService.addFromApi(patientNote, result);
    }

    /**
     * Mapping for GET
     *
     * Takes a PatientNote's ID, returns that PatientNote object
     *
     * Returns:
     * HttpStatus.NOT_FOUND if note cannot be found with provided ID
     * Json string & HttpStatus.OK if successful
     *
     * @param id
     * @return Json string & HttpStatus.CREATED if successful
     */
    @GetMapping("/patient/note/api/get/{id}")
    public ResponseEntity<String> getPatientNoteApi(@PathVariable("id") String id) {
        logger.info("User connected to /patient/note/get endpoint with id " + id);
        return patientNoteService.getFromApi(id);
    }

    /**
     * Mapping for GET
     *
     * Takes a Patient ID, returns all PatientNotes for that Patient
     *
     * Returns:
     * HttpStatus.NOT_FOUND if patient has no notes
     * Json string & HttpStatus.OK if successful
     *
     * @param patId Patient ID
     * @return Json string & HttpStatus.OK if successful
     */
    @GetMapping("/patient/note/api/getbypatient/{patId}")
    public ResponseEntity<String> getAllOnePatientNotesApi(@PathVariable("patId") int patId) {
        logger.info("User connected to /patient/note/api/getbypatient endpoint with id " + patId);
        return patientNoteService.getFromApiByPatientId(patId);
    }

    /**
     * Mapping for PUT
     *
     * Returns:
     * HttpStatus.NOT_FOUND if note does not exist with this ID
     * HttpStatus.BAD_REQUEST if note has errors
     * Json string & HttpStatus.OK if successful
     *
     * @param patientNote PatientNote with updated fields
     * @return Json string & HttpStatus.OK if successful
     */
    @PutMapping("/patient/note/api/update")
    public ResponseEntity<String> updatePatientApi(@Valid @RequestBody PatientNote patientNote, BindingResult result) {
        logger.info("User connected to /patient/add endpoint");
        return patientNoteService.updateFromApi(patientNote, result);
    }

    //Endpoints for serving Retrofit calls
    /**
     * Mapping for GET
     *
     * Intended to be called by other services, returns JSON string only
     * Takes a Patient ID, returns all PatientNotes for that Patient
     *
     *
     * @param id Patient ID
     * @return Json string
     */
    @GetMapping("/patient/note/api/retro/getbypatient/{id}")
    @ResponseBody
    public String getAllOnePatientNotesRetro(@PathVariable("id") int id) {
        logger.info("User connected to patient/note/api/retro/getbypatient/ endpoint with id " + id);
        return gson.toJson(patientNoteService.getFromApiByPatientIdRetro(id));
    }

}
