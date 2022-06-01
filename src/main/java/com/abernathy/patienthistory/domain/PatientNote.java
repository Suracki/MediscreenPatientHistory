package com.abernathy.patienthistory.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Document
public class PatientNote implements DomainElement {
    @Id
    private String patientNoteId;
    @Indexed
    @NotNull
    @Pattern(regexp = "^\\d+$",
    message="Patient ID must be an Integer value")
    private String patId;
    @NotEmpty(message="Note is mandatory")
    private String note;

    public String getPatientNoteId() {
        return patientNoteId;
    }

    public void setPatientNoteId(String patientNoteId) {
        this.patientNoteId = patientNoteId;
    }

    public String getId() {
        return patientNoteId;
    }

    public void setId(String patientNoteId) {
        this.patientNoteId = patientNoteId;
    }

    public String getPatId() {
        return patId;
    }

    public void setPatId(String patId) {
        this.patId = patId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public String toString() {
        return "{\"patientNote\":{\"patientNoteId\": \"" + patientNoteId +
                "\", \"patId\": \"" + patId +
                "\", \"note\": \"" + note +"\"}}";
    }
}

