package com.meda.model.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by isurud on 4/26/14.
 */
@Document(collection = "patient_registration")
public class PatientRegistrationDetails {

    @Id
    String _id;
    String pDestination;
    String dDestination;
    String dCode;

    public String get_id() {
        return _id;
    }

    public String getpDestination() {
        return pDestination;
    }

    public String getdDestination() {
        return dDestination;
    }

    public String getdCode() {
        return dCode;
    }
}
