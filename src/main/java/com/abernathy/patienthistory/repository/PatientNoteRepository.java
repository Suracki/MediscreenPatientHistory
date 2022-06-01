package com.abernathy.patienthistory.repository;

import com.abernathy.patienthistory.domain.PatientNote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PatientNoteRepository extends MongoRepository<PatientNote, String> {
    List<Optional<PatientNote>> findAllByPatId(int patId);
}
