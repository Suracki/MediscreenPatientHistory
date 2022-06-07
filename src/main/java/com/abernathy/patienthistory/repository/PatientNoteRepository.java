package com.abernathy.patienthistory.repository;

import com.abernathy.patienthistory.domain.PatientNote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface PatientNoteRepository extends MongoRepository<PatientNote, String> {
    @Query("{ 'patId' : '?0' }")
    List<PatientNote> findAllByPatId(int patId);

}
