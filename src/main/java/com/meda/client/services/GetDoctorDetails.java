package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.DoctorRegistrationDetails;
import config.MongoContextLoader;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.logging.Logger;

/**
 * Created by isurud on 4/27/14.
 */
public class GetDoctorDetails {

    String dCode;

    private static final Logger LOGGER = Logger.getLogger(GetDoctorDetails.class.getName());
    MongoContextLoader mongoContextLoader = new MongoContextLoader();


    public GetDoctorDetails(String dCode) {
        this.dCode = dCode;
    }

    public DoctorRegistrationDetails findDoctor() {

        String details;
        Gson gson = new Gson();

        Query findDoctorQuery = new Query(Criteria.where("_id").is(dCode));
        DoctorRegistrationDetails doctorRegistrationDetails = null;
        try {

            MongoOperations mongoOperations = mongoContextLoader.getMongoOperation();
            LOGGER.info("Finding doctor with id" + dCode);
            doctorRegistrationDetails = mongoOperations.findOne(findDoctorQuery, DoctorRegistrationDetails.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        details = gson.toJson(doctorRegistrationDetails);
        LOGGER.info("Got Details from Doctor Info  : " + details);
        return doctorRegistrationDetails;

    }
}
