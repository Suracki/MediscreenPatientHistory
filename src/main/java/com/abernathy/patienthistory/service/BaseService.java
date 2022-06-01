package com.abernathy.patienthistory.service;

import com.abernathy.patienthistory.domain.DomainElement;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;

public abstract class BaseService <E extends DomainElement> {

    private MongoRepository<E, String> repository;

    public BaseService(MongoRepository<E, String> repository) {
        this.repository = repository;
    }

    private String getType() {
        String className = getClass().getSimpleName().replace("Service","");
        return className.substring(0,1).toLowerCase() + className.substring(1);
    }

    //Methods to serve Front End requests

    /**
     * Method to get redirect for form to add a new element
     *
     * @param e DomainElement object of type to be added
     * @return url String
     */
    public String addForm(DomainElement e) {
        return getType() + "/add";
    }

    /**
     * Method to validate provided DomainElement
     * Adds DomainElement to repository if valid & updates model
     * Returns to form if any errors found
     *
     * @param e DomainElement object to be added
     * @param result BindingResult for validation
     * @param model Model object
     * @return url String
     */
    public String validate(@Valid E e, BindingResult result, Model model) {
        if (!result.hasErrors()){
            repository.save(e);
            model.addAttribute(getType() + "s", repository.findAll());
            return "redirect:/" + getType() + "/list";
        }
        return getType() + "/add";
    }

    //Methods to serve REST API requests

    /**
     * Method to validate provided DomainElement received via API post request
     * Adds DomainElement to repository if valid & updates model
     *
     * @param e DomainElement object to be added
     * @param result BindingResult for validation
     * @param model Model object
     * @return ResponseEntity JSON of added element and 201 if valid, 400 if invalid
     */
    public ResponseEntity<String> addFromApi(E e, BindingResult result, Model model) {
        if (!result.hasErrors()) {
            repository.save(e);
            model.addAttribute(getType() + "s", repository.findAll());
            return new ResponseEntity<String>(e.toString(), new HttpHeaders(), HttpStatus.CREATED);
        }

        return new ResponseEntity<String>("Failed to add new entry", new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

}
