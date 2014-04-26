package com.meda.model.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by isurud on 4/26/14.
 */
@Document(collection = "patient_registration")
public class RegistrationDetails {

    @Id
    String _id;
    String pDestination;
    String dDestination;
    String dCode;
}
