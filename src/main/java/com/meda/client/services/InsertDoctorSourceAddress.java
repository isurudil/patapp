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

    String details;
    Gson gson = new Gson();

    private static final Logger LOGGER = Logger.getLogger(InsertDoctorSourceAddress.class.getName());
    MongoContextLoader mongoContextLoader =   new MongoContextLoader();

    public DoctorRegistrationDetails insertDoctorSource(MoSmsReq moSmsReq,String appCode,String dCode){

        Query updatePatientDetailsQuery = new Query(Criteria.where("_id").is(appCode));
        DoctorRegistrationDetails doctorRegistrationDetails = null;
        try{
            MongoOperations mongoOperations = mongoContextLoader.getMongoOperation();
            Update update = new Update();
            doctorRegistrationDetails = mongoOperations.findOne(updatePatientDetailsQuery,DoctorRegistrationDetails.class);
            if(doctorRegistrationDetails != null){
                mongoOperations.updateFirst(updatePatientDetailsQuery,update.set("dDestination",moSmsReq.getSourceAddress()),PatientRegistrationDetails.class);
            }
            details= gson.toJson(doctorRegistrationDetails);
            LOGGER.info("Got Details from DB After Updating  : " + details);

        }catch (Exception ex){
            ex.printStackTrace();
        }

        return doctorRegistrationDetails;
    }
    public DoctorRegistrationDetails findDoctor(String dCode) {

        Query findDoctorQuery = new Query(Criteria.where("_id").is(dCode));
        DoctorRegistrationDetails doctorRegistrationDetails = null;
        try{

            MongoOperations mongoOperations =  mongoContextLoader.getMongoOperation();
            LOGGER.info("Finding doctor with id"+ dCode);
            doctorRegistrationDetails = mongoOperations.findOne(findDoctorQuery,DoctorRegistrationDetails.class);

        }catch (Exception e){
            e.printStackTrace();
        }
        details= gson.toJson(doctorRegistrationDetails);
        LOGGER.info("Got Details from DB After Updating Doctor Info  : " + details);
        return  doctorRegistrationDetails;

    }
}
