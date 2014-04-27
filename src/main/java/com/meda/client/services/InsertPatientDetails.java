package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.PatientRegistrationDetails;
import config.MongoContextLoader;

/**
 * Created by isurud on 4/27/14.
 */
public class InsertPatientDetails {

    String appCode;
    String dCode;
    String dDest;

    MongoContextLoader mongoContextLoader = new MongoContextLoader();
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(InsertPatientDetails.class.getName());
    String details;
    public InsertPatientDetails(String appCode, String dCode, String dDest) {
        this.appCode = appCode;
        this.dCode = dCode;
        this.dDest = dDest;
    }



    public PatientRegistrationDetails insertPatientRegistrationDetails() {
        Gson gson = new Gson();
        PatientRegistrationDetails patientRegistrationDetails = new PatientRegistrationDetails();
        GetPatientRegistrationDetails getPatientRegistrationDetails = new GetPatientRegistrationDetails(appCode);
        LOGGER.info("Getting appointment details : " + appCode);

        try {
            LOGGER.info("Setting id "+appCode);
            LOGGER.info("Id set,Setting dcode " + dCode);
            LOGGER.info("dcode set,setting D destination ");
            LOGGER.info("destination set,Setting P destination");
            patientRegistrationDetails.set_id(appCode);
            patientRegistrationDetails.setdCode(dCode);
            patientRegistrationDetails.setdDestination(dDest);
            patientRegistrationDetails.setpDestination("");
            LOGGER.info("Details are set");
            mongoContextLoader.getMongoOperation().save(patientRegistrationDetails);
            LOGGER.info("Save operation Executed, Getting updated details");
            patientRegistrationDetails = getPatientRegistrationDetails.getRegistrationDetails();
            details = gson.toJson(patientRegistrationDetails);
            LOGGER.info("Put Details to DB : " + details);
        } catch (Exception ex) {
            LOGGER.info(ex.getMessage());
            ex.printStackTrace();
        }
        return patientRegistrationDetails;
    }
}
