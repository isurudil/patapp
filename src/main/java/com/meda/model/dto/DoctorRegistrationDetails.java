package com.meda.model.dto;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by isurud on 4/26/14.
 */
@Document(collection = "doctor_info")
public class DoctorRegistrationDetails {

    @Id
    String _id;
    String dName;

    public String get_id() {
        return _id;
    }

    public String getdName() {
        return dName;
    }
}
