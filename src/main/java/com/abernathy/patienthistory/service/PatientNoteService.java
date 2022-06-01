package com.abernathy.patienthistory.service;

import com.abernathy.patienthistory.domain.PatientNote;
import com.abernathy.patienthistory.repository.PatientNoteRepository;
import org.springframework.stereotype.Service;

@Service
public class PatientNoteService extends BaseService<PatientNote>{
    public PatientNoteService(PatientNoteRepository repository) { super (repository);}
}
