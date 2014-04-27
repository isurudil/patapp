package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.DoctorRegistrationDetails;
import com.meda.model.dto.PatientRegistrationDetails;
import config.MongoContextLoader;
import hms.kite.samples.api.sms.messages.MoSmsReq;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.logging.Logger;

/**
 * Created by isurud on 4/26/14.
 */
public class InsertDoctorSourceAddress {

    MoSmsReq moSmsReq;
    String appCode;
    String dCode;
    String details;
    Gson gson = new Gson();
    String dName;

    public InsertDoctorSourceAddress(MoSmsReq moSmsReq, String appCode, String dCode) {
        this.moSmsReq = moSmsReq;
        this.appCode = appCode;
        this.dCode = dCode;
    }

    private static final Logger LOGGER = Logger.getLogger(InsertDoctorSourceAddress.class.getName());
    MongoContextLoader mongoContextLoader = new MongoContextLoader();

    public PatientRegistrationDetails insertDoctorSource() {

        Query updatePatientDetailsQuery = new Query(Criteria.where("_id").is(appCode));
        PatientRegistrationDetails patientRegistrationDetails = null;
        try {
            MongoOperations mongoOperations = mongoContextLoader.getMongoOperation();

            Update update = new Update();
            patientRegistrationDetails = mongoOperations.findOne(updatePatientDetailsQuery, PatientRegistrationDetails.class);
            if (patientRegistrationDetails != null) {
                LOGGER.info("patient details is not null");
                mongoOperations.updateFirst(updatePatientDetailsQuery, update.set("dDestination", moSmsReq.getSourceAddress()), PatientRegistrationDetails.class);
                mongoOperations.updateFirst(updatePatientDetailsQuery, update.set("dCode", dCode), PatientRegistrationDetails.class);
                dName = findDoctor().getdName();
                mongoOperations.updateFirst(updatePatientDetailsQuery, update.set("dName", dName), PatientRegistrationDetails.class);

            }
            else {
                InsertPatientDetails insertPatientDetails = new InsertPatientDetails(appCode,dCode,moSmsReq.getSourceAddress());
                patientRegistrationDetails = insertPatientDetails.insertPatientRegistrationDetails();
                LOGGER.info("################### In Else");
            }

            details = gson.toJson(patientRegistrationDetails);
            LOGGER.info("Got Details from DB After Updating  : " + details);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return patientRegistrationDetails;
    }

    public DoctorRegistrationDetails findDoctor() {

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
