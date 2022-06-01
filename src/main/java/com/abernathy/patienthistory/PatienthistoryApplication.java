package com.abernathy.patienthistory;

import com.abernathy.patienthistory.domain.PatientNote;
import com.abernathy.patienthistory.repository.PatientNoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class PatienthistoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(PatienthistoryApplication.class, args);
	}

//	@Bean
//	CommandLineRunner runner(PatientNoteRepository repository) {
//		return args -> {
//			addPatNote(repository);
//
//			List<Optional<PatientNote>> patientNotes = repository.findAllByPatId(13);
//			System.out.println("Notes for pat 13: " + patientNotes.size());
//
//		};
//	}
//
//	private void addPatNote(PatientNoteRepository repository) {
//		PatientNote patientNote = new PatientNote();
//		patientNote.setPatId(13);
//		patientNote.setNote("This Is A Test Note");
//
//
//		repository.insert(patientNote);
//	}

}
