package com.meda.client.services;

import com.google.gson.Gson;
import com.meda.model.dto.AppointmentDetails;
import config.MongoContextLoader;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Created by isurud on 4/26/14.
 */
@Component
public class GetAppointmentDetails {

    MongoContextLoader mongoContextLoader =   new MongoContextLoader();
    private static final Logger LOGGER = Logger.getLogger(GetAppointmentDetails.class);
    AppointmentDetails appointmentDetails;
    String details;

    public AppointmentDetails getAppoinmentDetails(String appCode){
        Gson gson = new Gson();
        Query searchUserQuery = new Query(Criteria.where("appointmentCode").is(appCode));
        LOGGER.debug("Getting patient details : " + appCode);

        try{
        appointmentDetails = mongoContextLoader.getMongoOperation().findOne(searchUserQuery,AppointmentDetails.class);
         details= gson.toJson(appointmentDetails);
        LOGGER.debug("Got Details from DB : "+details);
    }catch (Exception ex){

    }
        return appointmentDetails;
    }

}