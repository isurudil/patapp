package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.PatientRegistrationDetails;
import config.MongoContextLoader;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

/**
 * Created by isurud on 4/27/14.
 */
public class GetPatientRegistrationDetails {

    String appCode;
    String doctorCode;

    MongoContextLoader mongoContextLoader =   new MongoContextLoader();
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger.getLogger(GetPatientRegistrationDetails.class.getName());
    PatientRegistrationDetails patientRegistrationDetails;
    String details;

    public GetPatientRegistrationDetails(String appCode,String doctorCode) {
        this.appCode = appCode;
        this.doctorCode = doctorCode;
    }

    public PatientRegistrationDetails getRegistrationDetailsByAppCode(){
        Gson gson = new Gson();
        Query searchUserQuery = new Query(Criteria.where("_id").is(appCode));

        LOGGER.info("Getting patient details : " + appCode);

        try{
            patientRegistrationDetails = mongoContextLoader.getMongoOperation().findOne(searchUserQuery,PatientRegistrationDetails.class);
            details= gson.toJson(patientRegistrationDetails);
            LOGGER.info("Got Details from DB : "+details);
        }catch (Exception ex){
            ex.printStackTrace();

        }
        return patientRegistrationDetails;
    }

    public PatientRegistrationDetails getRegistrationDetailsByAppCodeAndDCode(){
        Gson gson = new Gson();
        Query searchUserQuery = new Query(Criteria.where("_id").is(appCode).andOperator(Criteria.where("dCode").is(doctorCode)));

        LOGGER.info("Getting patient details : " + appCode+" and " +doctorCode);

        try{
            patientRegistrationDetails = mongoContextLoader.getMongoOperation().findOne(searchUserQuery,PatientRegistrationDetails.class);
            details= gson.toJson(patientRegistrationDetails);
            LOGGER.info("Got Details from DB : "+details);
        }catch (Exception ex){
            ex.printStackTrace();

        }
        return patientRegistrationDetails;
    }



}
