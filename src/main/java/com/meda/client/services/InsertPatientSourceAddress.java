package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.RegistrationDetails;
import config.MongoContextLoader;
import hms.kite.samples.api.sms.messages.MoSmsReq;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Created by isurud on 4/26/14.
 */
public class InsertPatientSourceAddress {
    String details;
    Gson gson = new Gson();

    MongoContextLoader mongoContextLoader =   new MongoContextLoader();
    private static final Logger LOGGER = Logger.getLogger(InsertPatientSourceAddress.class);

    public RegistrationDetails insertPatientDestination(MoSmsReq moSmsReq,String appCode){

        Query updatePatientDetailsQuery = new Query(Criteria.where("_id").is(appCode));
        RegistrationDetails registrationDetails = null;


        try{
            MongoOperations mongoOperations = mongoContextLoader.getMongoOperation();
            Update update = new Update();
            registrationDetails = mongoOperations.findOne(updatePatientDetailsQuery,RegistrationDetails.class);
            if(registrationDetails != null){
                mongoOperations.updateFirst(updatePatientDetailsQuery,update.set("pDestination",moSmsReq.getSourceAddress()),RegistrationDetails.class);

            }
            details= gson.toJson(registrationDetails);
            LOGGER.info("Got Details from DB After Updating  : " + details);

        }catch (Exception ex){
           ex.printStackTrace();
        }

        return registrationDetails;
    }
}
